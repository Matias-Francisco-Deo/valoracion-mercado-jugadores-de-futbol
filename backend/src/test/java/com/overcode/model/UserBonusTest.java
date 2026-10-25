package com.overcode.model;

import com.overcode.model.bonus.LoginBonus;
import com.overcode.model.bonus.exception.BonusYaReclamadoHoyException;
import com.overcode.testUtils.TestLoginBonusUtil;
import com.overcode.testUtils.TestUserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class UserBonusTest {

    private User user;
    private LoginBonus loginBonus;

    @BeforeEach
    void setUp() {
        user = TestUserUtil.getUsuarioDeNombre("Pepito");
        loginBonus = TestLoginBonusUtil.getLoginBonusBase();
    }

    @Test
    void elBonusNuncaSeOtorgaSoloPorCrearOUsuarioLoguearse() {
        assertEquals(0.0, user.getCreditBalance());
        assertEquals(0, user.getStreakCount());
        assertNull(user.getLastBonusClaimDate());
    }

    @Test
    void alReclamarBonusDiarioPorPrimeraVezSeOtorgaBonus0YLaRachaEs1() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        loginBonus = spy(loginBonus);
        doReturn(hoy).when(loginBonus).getFechaActual();

        Double bonusOtorgado = user.reclamarBonusDiario(loginBonus).givenBonus();

        assertEquals(1.0, bonusOtorgado);
        assertEquals(1.0, user.getCreditBalance());
        assertEquals(1, user.getStreakCount());
        assertEquals(hoy, user.getLastBonusClaimDate());
    }

    @Test
    void alReclamarBonusEnDiasConsecutivosSeIncrementaLaRachaYElSaldo() {
        LocalDate dia1 = LocalDate.of(2026, 10, 21);
        LocalDate dia2 = LocalDate.of(2026, 10, 22);

        loginBonus = spy(loginBonus);
        doReturn(dia1).when(loginBonus).getFechaActual();

        user.reclamarBonusDiario(loginBonus);
        assertEquals(1.0, user.getCreditBalance());
        assertEquals(1, user.getStreakCount());

        doReturn(dia2).when(loginBonus).getFechaActual();

        Double bonusDia2 = user.reclamarBonusDiario(loginBonus).givenBonus();
        assertEquals(2.0, bonusDia2);
        assertEquals(3.0, user.getCreditBalance());
        assertEquals(2, user.getStreakCount());
        assertEquals(dia2, user.getLastBonusClaimDate());
    }

    @Test
    void alIntentarReclamarDosVecesElMismoDiaLanzaBonusYaReclamadoHoyExceptionYNoModificaSaldo() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        loginBonus = spy(loginBonus);
        doReturn(hoy).when(loginBonus).getFechaActual();

        user.reclamarBonusDiario(loginBonus);
        assertEquals(1.0, user.getCreditBalance());
        assertEquals(1, user.getStreakCount());

        assertThrows(BonusYaReclamadoHoyException.class, () ->
                user.reclamarBonusDiario(loginBonus)
        );

        assertEquals(1.0, user.getCreditBalance());
        assertEquals(1, user.getStreakCount());
        assertEquals(hoy, user.getLastBonusClaimDate());
    }
}
