package com.overcode.model.bonus;

import com.overcode.model.exception.bonus.BonusYaReclamadoHoyException;
import com.overcode.model.exception.bonus.FechaReclamoBonusLoginInvalidaException;
import com.overcode.model.user.User;
import com.overcode.testUtils.TestDailyBonusUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.overcode.testUtils.TestUserUtil.getUsuarioConRacha;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class DailyBonusTest {

    private DailyBonus dailyBonus;

    @BeforeEach
    void setUp() {
        dailyBonus = TestDailyBonusUtil.getDiaryBonusBase();
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
        User user = getUsuarioConRacha("pepito", 1, LocalDate.of(2026, 10, 22));
        dailyBonus = spy(dailyBonus);
        doReturn(LocalDate.of(2026, 10, 23)).when(dailyBonus).getFechaActual();

        assertEquals(2.0, dailyBonus.getBonusParaUser(user));
    }

    @Test
    void alCalcularSiguienteRachaSinReclamoPrevioRetorna1() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);
        User user = getUsuarioConRacha("pepito", 0, hoy);

        int siguienteRacha = user.aumentarRacha();

        assertEquals(1, siguienteRacha);
    }

    @Test
    void alCalcularSiguienteRachaEnDiasConsecutivosSeIncrementaLaRacha() {
        LocalDate ayer = LocalDate.of(2026, 10, 21);
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        User user1 = getUsuarioConRacha("pepito", 1, ayer);

        dailyBonus = spy(dailyBonus);
        doReturn(hoy).when(dailyBonus).getFechaActual();

        assertEquals(2, user1.aumentarRacha());

        user1.setStreakCount(2);

        assertEquals(3, user1.aumentarRacha());

        user1.setStreakCount(6);

        assertEquals(0, user1.aumentarRacha());
    }

    @Test
    void alPasarMasDeUnDiaSinReclamarSeReiniciaLaRachaADia1() {
        LocalDate haceTresDias = LocalDate.of(2026, 10, 19);
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        User user1 = getUsuarioConRacha("pepito", 4, haceTresDias);

        dailyBonus = spy(dailyBonus);
        doReturn(hoy).when(dailyBonus).getFechaActual();

        user1.reclamarBonusDiario(dailyBonus);
        assertEquals(1, user1.getStreakCount());
    }

    @Test
    void trasInactividadProlongadaSeReiniciaLaRachaADia1SinErrores() {
        LocalDate haceUnMes = LocalDate.of(2026, 9, 22);
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        User user1 = getUsuarioConRacha("pepito", 5, haceUnMes);

        dailyBonus = spy(dailyBonus);
        doReturn(hoy).when(dailyBonus).getFechaActual();

        user1.reclamarBonusDiario(dailyBonus);
        assertEquals(1, user1.getStreakCount());
    }

    @Test
    void alReclamarEnElDia8LaRachaSeReiniciaADia1CicloSemanal() {
        LocalDate ayer = LocalDate.of(2026, 10, 21);
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        User user1 = getUsuarioConRacha("pepito", 6, ayer);

        dailyBonus = spy(dailyBonus);
        doReturn(hoy).when(dailyBonus).getFechaActual();
        user1 = spy(user1);
        doReturn(hoy).when(user1).getFechaActual();

        user1.reclamarBonusDiario(dailyBonus);
        assertEquals(0, user1.getStreakCount());
    }

    @Test
    void alIntentarReclamarDosVecesElMismoDiaLanzaBonusYaReclamadoHoyException() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        User user1 = getUsuarioConRacha("pepito", 1, hoy);

        dailyBonus = spy(dailyBonus);
        doReturn(hoy).when(dailyBonus).getFechaActual();

        assertThrows(BonusYaReclamadoHoyException.class, () ->
                user1.reclamarBonusDiario(dailyBonus)
        );
    }

    @Test
    void alCalcularRachaConFechaActualAnteriorAUltimoReclamoLanzaFechaReclamoInvalidaException() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);
        LocalDate ayer = LocalDate.of(2026, 10, 21);

        User user1 = getUsuarioConRacha("pepito", 1, hoy);

        dailyBonus = spy(dailyBonus);
        doReturn(ayer).when(dailyBonus).getFechaActual();

        assertThrows(FechaReclamoBonusLoginInvalidaException.class, () ->
                dailyBonus.verificarPuedeReclamarBonus(user1)
        );
    }

    @Test
    void yaReclamoBonusRetornaFalseSiNoHayReclamoPrevio() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        User user1 = getUsuarioConRacha("pepito", 1, null);

        assertFalse(user1.yaReclamoBonus(hoy));
    }

    @Test
    void yaReclamoBonusRetornaFalseSiYaReclamoHoy() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        User user1 = getUsuarioConRacha("pepito", 1, hoy);

        dailyBonus = spy(dailyBonus);
        doReturn(hoy).when(dailyBonus).getFechaActual();

        assertTrue(user1.yaReclamoBonus(hoy));
    }

    @Test
    void yaReclamoBonusRetornaTrueSiReclamoAyer() {
        LocalDate ayer = LocalDate.of(2026, 10, 21);
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        User user1 = getUsuarioConRacha("pepito", 1, ayer);

        assertFalse(user1.yaReclamoBonus(hoy));
    }
}
