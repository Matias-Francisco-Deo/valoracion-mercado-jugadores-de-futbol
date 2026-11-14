package com.overcode.model.bonus;

import com.overcode.config.Reloj;
import com.overcode.model.exception.bonus.BonusYaReclamadoHoyException;
import com.overcode.model.exception.bonus.FechaReclamoBonusLoginInvalidaException;
import com.overcode.model.exception.bonus.mission.RequisitosMisionesNoCumplidosException;
import com.overcode.model.user.User;
import com.overcode.testUtils.TestDailyBonusUtil;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalDate;

import static com.overcode.testUtils.TestDailyBonusUtil.getDailyBonusBase;
import static com.overcode.testUtils.TestUserUtil.getUsuarioConRachaYMision;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

class DailyBonusTest {

    private DailyBonus dailyBonus;

    @BeforeEach
    void setUp() {
        dailyBonus = TestDailyBonusUtil.getDailyBonusBaseYaCompletado();
    }

    @Test
    void alConsultarBonusParaCadaDiaRetornaElValorConfigurado() {
        assertEquals(1.0, dailyBonus.getBonusConRacha(0));
        assertEquals(2.0, dailyBonus.getBonusConRacha(1));
        assertEquals(2.0, dailyBonus.getBonusConRacha(2));
        assertEquals(5.0, dailyBonus.getBonusConRacha(3));
        assertEquals(5.0, dailyBonus.getBonusConRacha(4));
        assertEquals(10.0, dailyBonus.getBonusConRacha(5));
        assertEquals(15.0, dailyBonus.getBonusConRacha(6));
    }

    @Test
    void alConsultarBonusParaRachaRetornaElValorCorrespondiente() {
        LocalDate hoy = LocalDate.of(2026, 10, 23);
        User user = getUsuarioTestConFechaYRacha(hoy.minusDays(1), 1);

        try (MockedStatic<Reloj> reloj = mockStatic(Reloj.class)) {
            reloj.when(Reloj::getLocalDate).thenReturn(hoy);

            assertEquals(2.0, dailyBonus.getBonusParaUser(user.getUserDailyBonus()));
        }
    }


    @Test
    void alCalcularSiguienteRachaSinReclamoPrevioRetorna1() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);
        User user = getUsuarioTestConFechaYRacha(hoy, 0);

        int siguienteRacha = user.getUserDailyBonus().aumentarRacha();

        assertEquals(1, siguienteRacha);
    }

    @Test
    void alCalcularSiguienteRachaEnDiasConsecutivosSeIncrementaLaRacha() {
        LocalDate ayer = LocalDate.of(2026, 10, 21);
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        User user1 = getUsuarioTestConFechaYRacha(ayer, 1);
        UserDailyBonus user1UserDailyBonus = user1.getUserDailyBonus();

        try (MockedStatic<Reloj> reloj = mockStatic(Reloj.class)) {
            reloj.when(Reloj::getLocalDate).thenReturn(hoy);

            assertEquals(2, user1UserDailyBonus.aumentarRacha());

            user1UserDailyBonus.setStreakCount(2);

            assertEquals(3, user1UserDailyBonus.aumentarRacha());

            user1UserDailyBonus.setStreakCount(6);

            assertEquals(0, user1UserDailyBonus.aumentarRacha());
        }
    }

    @Test
    void alPasarMasDeUnDiaSinReclamarSeReiniciaLaRachaADia1() {
        LocalDate haceTresDias = LocalDate.of(2026, 10, 19);
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        User user1 = getUsuarioTestConFechaYRacha(haceTresDias, 4);
        UserDailyBonus user1UserDailyBonus = user1.getUserDailyBonus();

        try (MockedStatic<Reloj> reloj = mockStatic(Reloj.class)) {
            reloj.when(Reloj::getLocalDate).thenReturn(hoy);

            user1UserDailyBonus.reclamarBonus(dailyBonus);
            assertEquals(1, user1UserDailyBonus.getStreakCount());
        }
    }

    @Test
    void trasInactividadProlongadaSeReiniciaLaRachaADia1SinErrores() {
        LocalDate haceUnMes = LocalDate.of(2026, 9, 22);
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        User user1 = getUsuarioTestConFechaYRacha(haceUnMes, 5);
        UserDailyBonus user1UserDailyBonus = user1.getUserDailyBonus();

        try (MockedStatic<Reloj> reloj = mockStatic(Reloj.class)) {
            reloj.when(Reloj::getLocalDate).thenReturn(hoy);

            user1UserDailyBonus.reclamarBonus(dailyBonus);
            assertEquals(1, user1UserDailyBonus.getStreakCount());
        }
    }

    @Test
    void alReclamarEnElDia8LaRachaSeReiniciaADia1CicloSemanal() {
        LocalDate ayer = LocalDate.of(2026, 10, 21);
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        User user1 = getUsuarioTestConFechaYRacha(ayer, 6);
        UserDailyBonus user1UserDailyBonus = user1.getUserDailyBonus();

        try (MockedStatic<Reloj> reloj = mockStatic(Reloj.class)) {
            reloj.when(Reloj::getLocalDate).thenReturn(hoy);

            user1UserDailyBonus.reclamarBonus(dailyBonus);
            assertEquals(0, user1UserDailyBonus.getStreakCount());
        }
    }

    @Test
    void alIntentarReclamarDosVecesElMismoDiaLanzaBonusYaReclamadoHoyException() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        User user1 = getUsuarioTestConFechaYRacha(hoy, 1);
        UserDailyBonus user1UserDailyBonus = user1.getUserDailyBonus();

        try (MockedStatic<Reloj> reloj = mockStatic(Reloj.class)) {
            reloj.when(Reloj::getLocalDate).thenReturn(hoy);

            assertThrows(BonusYaReclamadoHoyException.class, () ->
                    user1UserDailyBonus.reclamarBonus(dailyBonus)
            );
        }
    }

    @Test
    void alCalcularRachaConFechaActualAnteriorAUltimoReclamoLanzaFechaReclamoInvalidaException() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);
        LocalDate ayer = LocalDate.of(2026, 10, 21);

        User user1 = getUsuarioTestConFechaYRacha(hoy, 1);
        UserDailyBonus user1UserDailyBonus = user1.getUserDailyBonus();

        try (MockedStatic<Reloj> reloj = mockStatic(Reloj.class)) {
            reloj.when(Reloj::getLocalDate).thenReturn(ayer);

            assertThrows(FechaReclamoBonusLoginInvalidaException.class, () ->
                    dailyBonus.verificarPuedeReclamarBonus(user1UserDailyBonus)
            );
        }
    }

    @Test
    void yaReclamoBonusRetornaFalseSiNoHayReclamoPrevio() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        User user1 = getUsuarioTestConFechaYRacha(null, 1);
        UserDailyBonus user1UserDailyBonus = user1.getUserDailyBonus();

        assertFalse(user1UserDailyBonus.yaReclamoBonusHoy(hoy));
    }

    @Test
    void yaReclamoBonusRetornaFalseSiYaReclamoHoy() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        User user1 = getUsuarioTestConFechaYRacha(hoy, 1);
        UserDailyBonus user1UserDailyBonus = user1.getUserDailyBonus();

        assertTrue(user1UserDailyBonus.yaReclamoBonusHoy(hoy));
    }

    @Test
    void yaReclamoBonusRetornaTrueSiReclamoAyer() {
        LocalDate ayer = LocalDate.of(2026, 10, 21);
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        User user1 = getUsuarioTestConFechaYRacha(ayer, 1);
        UserDailyBonus user1UserDailyBonus = user1.getUserDailyBonus();

        assertFalse(user1UserDailyBonus.yaReclamoBonusHoy(hoy));
    }

    @Test
    void noPuedeReclamarSiLaMisionNoFueCumplida() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);
        LocalDate ayer = LocalDate.of(2026, 10, 21);

        dailyBonus = getDailyBonusBase();

        User user1 = getUsuarioConRachaYMision("pepito", 0, ayer, dailyBonus.getGetRootMission());
        UserDailyBonus user1UserDailyBonus = user1.getUserDailyBonus();

        try (MockedStatic<Reloj> reloj = mockStatic(Reloj.class)) {
            reloj.when(Reloj::getLocalDate).thenReturn(hoy);

            assertThrows(RequisitosMisionesNoCumplidosException.class, () ->
                    dailyBonus.verificarPuedeReclamarBonus(user1UserDailyBonus)
            );
        }
    }

    private @NonNull User getUsuarioTestConFechaYRacha(LocalDate ultimoReclamo, int racha) {
        return getUsuarioConRachaYMision("pepito", racha, ultimoReclamo, dailyBonus.getGetRootMission());
    }
}
