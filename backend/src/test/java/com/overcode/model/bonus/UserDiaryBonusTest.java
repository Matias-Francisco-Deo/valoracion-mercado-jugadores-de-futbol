package com.overcode.model.bonus;

import com.overcode.model.bonus.mission.BuyMission;
import com.overcode.model.exception.bonus.BonusYaReclamadoHoyException;
import com.overcode.model.exception.bonus.FechaReclamoBonusDiarioInvalidaException;
import com.overcode.model.exception.bonus.RequisitosMisionesNoCumplidosException;
import com.overcode.model.user.User;
import com.overcode.testUtils.TestDiaryBonusUtil;
import com.overcode.testUtils.TestUserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class UserDiaryBonusTest {

    private DiaryBonus diaryBonus;
    private User user;
    private UserDiaryBonus userDiaryBonus;

    @BeforeEach
    void setUp() {
        diaryBonus = TestDiaryBonusUtil.getDiaryBonusBase();
        user = TestUserUtil.getUsuarioBase("carlos");
        user.setCreditBalance(100.0);
        userDiaryBonus = new UserDiaryBonus(user);
    }

    @Test
    void alCalcularSiguienteRachaEnDiasConsecutivosSeIncrementaLaRachaYReiniciaEnDia7() {
        LocalDate ayer = LocalDate.of(2026, 11, 5);
        LocalDate hoy = LocalDate.of(2026, 11, 6);

        userDiaryBonus = spy(userDiaryBonus);
        doReturn(hoy).when(userDiaryBonus).getFechaActual();

        userDiaryBonus.setLastBonusClaimDate(ayer);
        userDiaryBonus.setStreakCount(0);
        assertEquals(1, userDiaryBonus.aumentarRacha());

        userDiaryBonus.setStreakCount(5);
        assertEquals(6, userDiaryBonus.aumentarRacha());

        userDiaryBonus.setStreakCount(6);
        assertEquals(0, userDiaryBonus.aumentarRacha());
    }

    @Test
    void alPasarMasDeUnDiaSinReclamarSeReiniciaLaRachaACero() {
        LocalDate haceTresDias = LocalDate.of(2026, 11, 3);
        LocalDate hoy = LocalDate.of(2026, 11, 6);

        userDiaryBonus = spy(userDiaryBonus);
        doReturn(hoy).when(userDiaryBonus).getFechaActual();

        userDiaryBonus.setLastBonusClaimDate(haceTresDias);
        userDiaryBonus.setStreakCount(4);

        userDiaryBonus.actualizarRacha();
        assertEquals(0, userDiaryBonus.getStreakCount());
    }

    @Test
    void alIntentarReclamarSinCumplirMisionesLanzaRequisitosMisionesNoCumplidosException() {
        BuyMission buyMission = new BuyMission();
        userDiaryBonus.setActiveMission(buyMission);

        assertThrows(RequisitosMisionesNoCumplidosException.class, () ->
                userDiaryBonus.reclamarBonus(diaryBonus)
        );
    }

    @Test
    void alReclamarBonusDiarioConMisionCompletadaSeIncrementaCreditoYSeAumentaRacha() {
        BuyMission buyMission = new BuyMission();
        buyMission.complete();
        userDiaryBonus.setActiveMission(buyMission);
        userDiaryBonus.setStreakCount(0);

        BonusClaim claim = userDiaryBonus.reclamarBonus(diaryBonus);

        assertEquals(1.0, claim.givenBonus());
        assertEquals(1, claim.currentStreak());
        assertEquals(101.0, claim.currentCreditBalance());
        assertEquals(101.0, user.getCreditBalance());
        assertEquals(1, userDiaryBonus.getStreakCount());
    }

    @Test
    void alIntentarReclamarDosVecesElMismoDiaLanzaBonusYaReclamadoHoyException() {
        BuyMission buyMission = new BuyMission();
        buyMission.complete();
        userDiaryBonus.setActiveMission(buyMission);

        userDiaryBonus.reclamarBonus(diaryBonus);

        assertThrows(BonusYaReclamadoHoyException.class, () ->
                userDiaryBonus.reclamarBonus(diaryBonus)
        );
    }

    @Test
    void alCalcularRachaConFechaAnteriorAUltimoReclamoLanzaExcepcion() {
        LocalDate hoy = LocalDate.of(2026, 11, 6);
        LocalDate ayer = LocalDate.of(2026, 11, 5);

        userDiaryBonus = spy(userDiaryBonus);
        doReturn(ayer).when(userDiaryBonus).getFechaActual();

        userDiaryBonus.setLastBonusClaimDate(hoy);

        assertThrows(FechaReclamoBonusDiarioInvalidaException.class, () ->
                userDiaryBonus.actualizarRacha()
        );
    }
}
