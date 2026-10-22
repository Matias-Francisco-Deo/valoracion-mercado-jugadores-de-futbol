package com.overcode.model;

import com.overcode.model.bonus.LoginBonus;
import com.overcode.model.bonus.exception.BonusYaReclamadoHoyException;
import com.overcode.model.bonus.exception.LoginBonusInvalidoException;
import com.overcode.testUtils.TestLoginBonusUtil;
import com.overcode.testUtils.TestUserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserBonusTest {

    private User user;
    private LoginBonus loginBonus;

    @BeforeEach
    void setUp() {
        user = TestUserUtil.getUsuarioDeNombre("Matias");
        loginBonus = TestLoginBonusUtil.getLoginBonusBase();
    }

    @Test
    void elBonusNuncaSeOtorgaSoloPorCrearOUsuarioLoguearse() {
        assertEquals(0.0, user.getCreditBalance());
        assertEquals(0, user.getStreakCount());
        assertNull(user.getLastBonusClaimDate());
    }

    @Test
    void alReclamarBonusDiarioPorPrimeraVezSeOtorgaBonusLunesYLaRachaEs1() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        Double bonusOtorgado = user.reclamarBonusDiario(hoy, loginBonus);

        assertEquals(1.0, bonusOtorgado);
        assertEquals(1.0, user.getCreditBalance());
        assertEquals(1, user.getStreakCount());
        assertEquals(hoy, user.getLastBonusClaimDate());
    }

    @Test
    void alReclamarBonusEnDiasConsecutivosSeIncrementaLaRachaYElSaldo() {
        LocalDate dia1 = LocalDate.of(2026, 10, 21);
        LocalDate dia2 = LocalDate.of(2026, 10, 22);

        user.reclamarBonusDiario(dia1, loginBonus);
        assertEquals(1.0, user.getCreditBalance());
        assertEquals(1, user.getStreakCount());

        Double bonusDia2 = user.reclamarBonusDiario(dia2, loginBonus);
        assertEquals(2.0, bonusDia2);
        assertEquals(3.0, user.getCreditBalance());
        assertEquals(2, user.getStreakCount());
        assertEquals(dia2, user.getLastBonusClaimDate());
    }

    @Test
    void alIntentarReclamarConLoginBonusNuloLanzaLoginBonusInvalidoException() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);
        assertThrows(LoginBonusInvalidoException.class, () -> user.reclamarBonusDiario(hoy, null));
    }

    @Test
    void alIntentarReclamarDosVecesElMismoDiaLanzaBonusYaReclamadoHoyExceptionYNoModificaSaldo() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        user.reclamarBonusDiario(hoy, loginBonus);
        assertEquals(1.0, user.getCreditBalance());
        assertEquals(1, user.getStreakCount());

        assertThrows(BonusYaReclamadoHoyException.class, () ->
                user.reclamarBonusDiario(hoy, loginBonus)
        );

        assertEquals(1.0, user.getCreditBalance());
        assertEquals(1, user.getStreakCount());
        assertEquals(hoy, user.getLastBonusClaimDate());
    }
}
