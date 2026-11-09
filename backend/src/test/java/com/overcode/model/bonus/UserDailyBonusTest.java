package com.overcode.model.bonus;

import com.overcode.model.bonus.mission.BuyDailyBonusMission;
import com.overcode.model.exception.bonus.BonusYaReclamadoHoyException;
import com.overcode.model.exception.bonus.FechaReclamoBonusDiarioInvalidaException;
import com.overcode.model.exception.bonus.RequisitosMisionesNoCumplidosException;
import com.overcode.model.user.User;
import com.overcode.testUtils.TestDailyBonusUtil;
import com.overcode.testUtils.TestUserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class UserDailyBonusTest {

    private DailyBonus dailyBonus;
    private User user;
    private UserDailyBonus userDailyBonus;

    @BeforeEach
    void setUp() {
        dailyBonus = TestDailyBonusUtil.getDiaryBonusBase();
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
}
