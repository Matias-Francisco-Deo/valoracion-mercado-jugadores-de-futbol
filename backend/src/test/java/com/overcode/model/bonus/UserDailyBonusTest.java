package com.overcode.model.bonus;

import com.overcode.config.Reloj;
import com.overcode.model.bonus.mission.BuyDailyBonusMission;
import com.overcode.model.exception.bonus.BonusYaReclamadoHoyException;
import com.overcode.model.exception.bonus.FechaReclamoBonusDiarioInvalidaException;
import com.overcode.model.exception.bonus.RequisitosMisionesNoCumplidosException;
import com.overcode.model.user.User;
import com.overcode.testUtils.TestDailyBonusUtil;
import com.overcode.testUtils.TestUserUtil;
import com.overcode.testUtils.bonus.mission.TestDailyBonusMission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.overcode.testUtils.bonus.mission.TestMissionUtil.crearTestMission;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class UserDailyBonusTest {

    private DailyBonus dailyBonus;
    private User user;
    private UserDailyBonus userDailyBonus;

    @BeforeEach
    void setUp() {
        dailyBonus = TestDailyBonusUtil.getDailyBonusBase();
        user = TestUserUtil.getUsuarioBase("carlos");
        user.setCreditBalance(100.0);
        userDailyBonus = new UserDailyBonus(user);
    }

    @Test
    void alCalcularSiguienteRachaEnDiasConsecutivosSeIncrementaLaRachaYReiniciaEnDia7() {
        LocalDate ayer = LocalDate.of(2026, 11, 5);
        LocalDate hoy = LocalDate.of(2026, 11, 6);

        userDailyBonus = spy(userDailyBonus);
        doReturn(hoy).when(userDailyBonus).getFechaActual();

        userDailyBonus.setLastBonusClaimDate(ayer);
        userDailyBonus.setStreakCount(0);
        assertEquals(1, userDailyBonus.aumentarRacha());

        userDailyBonus.setStreakCount(5);
        assertEquals(6, userDailyBonus.aumentarRacha());

        userDailyBonus.setStreakCount(6);
        assertEquals(0, userDailyBonus.aumentarRacha());
    }

    @Test
    void alPasarMasDeUnDiaSinReclamarSeReiniciaLaRachaACero() {
        LocalDate haceTresDias = LocalDate.of(2026, 11, 3);
        LocalDate hoy = LocalDate.of(2026, 11, 6);

        userDailyBonus = spy(userDailyBonus);
        doReturn(hoy).when(userDailyBonus).getFechaActual();

        userDailyBonus.setLastBonusClaimDate(haceTresDias);
        userDailyBonus.setStreakCount(4);

        userDailyBonus.actualizarRacha();
        assertEquals(0, userDailyBonus.getStreakCount());
    }

    @Test
    void alIntentarReclamarSinCumplirMisionesLanzaRequisitosMisionesNoCumplidosException() {
        BuyDailyBonusMission buyMission = new BuyDailyBonusMission();
        userDailyBonus.setActiveDailyBonusMission(buyMission);

        assertThrows(RequisitosMisionesNoCumplidosException.class, () ->
                userDailyBonus.reclamarBonus(dailyBonus)
        );
    }


    @Test
    void alReclamarBonusDiarioConMisionCompletadaSeIncrementaCreditoYSeAumentaRacha() {
        BuyDailyBonusMission buyMission = new BuyDailyBonusMission();
        buyMission.complete();
        userDailyBonus.setActiveDailyBonusMission(buyMission);
        userDailyBonus.setActiveMissionDate(Reloj.getLocalDate());
        userDailyBonus.setStreakCount(0);

        BonusClaim claim = userDailyBonus.reclamarBonus(dailyBonus);

        assertEquals(1.0, claim.givenBonus());
        assertEquals(1, claim.currentStreak());
        assertEquals(101.0, claim.currentCreditBalance());
        assertEquals(101.0, user.getCreditBalance());
        assertEquals(1, userDailyBonus.getStreakCount());
    }

    @Test
    void alIntentarReclamarDosVecesElMismoDiaLanzaBonusYaReclamadoHoyException() {
        BuyDailyBonusMission buyMission = new BuyDailyBonusMission();
        buyMission.complete();
        userDailyBonus.setActiveDailyBonusMission(buyMission);
        userDailyBonus.setActiveMissionDate(Reloj.getLocalDate());

        userDailyBonus.reclamarBonus(dailyBonus);

        assertThrows(BonusYaReclamadoHoyException.class, () ->
                userDailyBonus.reclamarBonus(dailyBonus)
        );
    }

    @Test
    void alCalcularRachaConFechaAnteriorAUltimoReclamoLanzaExcepcion() {
        LocalDate hoy = LocalDate.of(2026, 11, 6);
        LocalDate ayer = LocalDate.of(2026, 11, 5);

        userDailyBonus = spy(userDailyBonus);
        doReturn(ayer).when(userDailyBonus).getFechaActual();

        userDailyBonus.setLastBonusClaimDate(hoy);

        assertThrows(FechaReclamoBonusDiarioInvalidaException.class, () ->
                userDailyBonus.actualizarRacha()
        );
    }

    

    @Test
    void alActualizarMisionInsertaMisionClonadaSiNoTenia() {
        userDailyBonus.setActiveMissionDate(null);
        userDailyBonus.setActiveDailyBonusMission(null);

        userDailyBonus.actualizarMision(dailyBonus);

        assertEquals(userDailyBonus.getActiveMissionDate(), Reloj.getLocalDate());
        assertNotNull(userDailyBonus.getActiveDailyBonusMission());
        assertNotEquals(userDailyBonus.getActiveDailyBonusMission(), dailyBonus.getRootDailyBonusMission());
    }

    @Test
    void alActualizarMisionInsertaMisionClonadaSiPasaUnDia() {
        LocalDate ayer = LocalDate.of(2026, 11, 5);
        userDailyBonus.setActiveMissionDate(ayer);
        TestDailyBonusMission misionVieja = crearTestMission("mision vieja");
        userDailyBonus.setActiveDailyBonusMission(misionVieja);

        userDailyBonus.actualizarMision(dailyBonus);

        assertEquals(userDailyBonus.getActiveMissionDate(), Reloj.getLocalDate());
        assertNotNull(userDailyBonus.getActiveDailyBonusMission());
        assertNotEquals(userDailyBonus.getActiveDailyBonusMission(), dailyBonus.getRootDailyBonusMission());
        assertNotEquals(userDailyBonus.getActiveDailyBonusMission(), misionVieja);
    }

    @Test
    void alActualizarMisionInsertaMisionClonadaSiPasanVariosDias() {
        LocalDate variosDiasAtras = LocalDate.of(2026, 11, 1);
        TestDailyBonusMission misionVieja = crearTestMission("mision vieja");

        userDailyBonus.setActiveMissionDate(variosDiasAtras);
        userDailyBonus.setActiveDailyBonusMission(misionVieja);

        userDailyBonus.actualizarMision(dailyBonus);

        assertEquals(userDailyBonus.getActiveMissionDate(), Reloj.getLocalDate());
        assertNotNull(userDailyBonus.getActiveDailyBonusMission());
        assertNotEquals(userDailyBonus.getActiveDailyBonusMission(), dailyBonus.getRootDailyBonusMission());
        assertNotEquals(userDailyBonus.getActiveDailyBonusMission(), misionVieja);
    }

    @Test
    void alReclamarBonusSeActualizaLaMision() {
        BuyDailyBonusMission buyMission = new BuyDailyBonusMission();
        buyMission.complete();
        userDailyBonus.setActiveDailyBonusMission(buyMission);
        userDailyBonus.setActiveMissionDate(Reloj.getLocalDate());
        userDailyBonus = spy(userDailyBonus);

        userDailyBonus.reclamarBonus(dailyBonus);

        verify(userDailyBonus).actualizarMision(dailyBonus);
    }

    @Test
    void alRegistrarCompraSeActualizaLaMision() {
        userDailyBonus = spy(userDailyBonus);
        doNothing().when(userDailyBonus).actualizarMision(dailyBonus);

        userDailyBonus.registrarCompra(dailyBonus);

        verify(userDailyBonus).actualizarMision(dailyBonus);
    }

    @Test
    void alRegistrarVisitaSeActualizaLaMision() {
        userDailyBonus = spy(userDailyBonus);
        doNothing().when(userDailyBonus).actualizarMision(dailyBonus);

        userDailyBonus.registrarVisitaJugador(null, dailyBonus);

        verify(userDailyBonus).actualizarMision(dailyBonus);
    }

    @Test
    void alObtenerEstadoBonusSeActualizaLaMision() {
        BuyDailyBonusMission buyMission = new BuyDailyBonusMission();
        buyMission.complete();
        userDailyBonus.setActiveDailyBonusMission(buyMission);
        userDailyBonus.setActiveMissionDate(Reloj.getLocalDate());
        userDailyBonus = spy(userDailyBonus);

        dailyBonus.obtenerEstadoBonus(userDailyBonus);

        verify(userDailyBonus).actualizarMision(dailyBonus);
    }
}
