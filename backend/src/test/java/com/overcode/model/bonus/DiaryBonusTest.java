package com.overcode.model.bonus;

import com.overcode.model.exception.bonus.BonusYaReclamadoHoyException;
import com.overcode.model.exception.bonus.FechaReclamoBonusLoginInvalidaException;
import com.overcode.model.user.User;
import com.overcode.testUtils.TestDiaryBonusUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.overcode.testUtils.TestUserUtil.getUsuarioConRacha;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class DiaryBonusTest {

    private DiaryBonus diaryBonus;

    @BeforeEach
    void setUp() {
        diaryBonus = TestDiaryBonusUtil.getDiaryBonusBase();
    }

    @Test
    void alConsultarBonusParaCadaDiaRetornaElValorConfigurado() {
        assertEquals(1.0, diaryBonus.getBonusConRacha(0));
        assertEquals(2.0, diaryBonus.getBonusConRacha(1));
        assertEquals(2.0, diaryBonus.getBonusConRacha(2));
        assertEquals(5.0, diaryBonus.getBonusConRacha(3));
        assertEquals(5.0, diaryBonus.getBonusConRacha(4));
        assertEquals(10.0, diaryBonus.getBonusConRacha(5));
        assertEquals(15.0, diaryBonus.getBonusConRacha(6));
    }

    @Test
    void alConsultarBonusParaRachaRetornaElValorCorrespondiente() {
        User user = getUsuarioConRacha("pepito", 1, LocalDate.of(2026, 10, 22));
        diaryBonus = spy(diaryBonus);
        doReturn(LocalDate.of(2026, 10, 23)).when(diaryBonus).getFechaActual();

        assertEquals(2.0, diaryBonus.getBonusParaUser(user));
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

        diaryBonus = spy(diaryBonus);
        doReturn(hoy).when(diaryBonus).getFechaActual();

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

        diaryBonus = spy(diaryBonus);
        doReturn(hoy).when(diaryBonus).getFechaActual();

        user1.reclamarBonusDiario(diaryBonus);
        assertEquals(1, user1.getStreakCount());
    }

    @Test
    void trasInactividadProlongadaSeReiniciaLaRachaADia1SinErrores() {
        LocalDate haceUnMes = LocalDate.of(2026, 9, 22);
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        User user1 = getUsuarioConRacha("pepito", 5, haceUnMes);

        diaryBonus = spy(diaryBonus);
        doReturn(hoy).when(diaryBonus).getFechaActual();

        user1.reclamarBonusDiario(diaryBonus);
        assertEquals(1, user1.getStreakCount());
    }

    @Test
    void alReclamarEnElDia8LaRachaSeReiniciaADia1CicloSemanal() {
        LocalDate ayer = LocalDate.of(2026, 10, 21);
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        User user1 = getUsuarioConRacha("pepito", 6, ayer);

        diaryBonus = spy(diaryBonus);
        doReturn(hoy).when(diaryBonus).getFechaActual();
        user1 = spy(user1);
        doReturn(hoy).when(user1).getFechaActual();

        user1.reclamarBonusDiario(diaryBonus);
        assertEquals(0, user1.getStreakCount());
    }

    @Test
    void alIntentarReclamarDosVecesElMismoDiaLanzaBonusYaReclamadoHoyException() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        User user1 = getUsuarioConRacha("pepito", 1, hoy);

        diaryBonus = spy(diaryBonus);
        doReturn(hoy).when(diaryBonus).getFechaActual();

        assertThrows(BonusYaReclamadoHoyException.class, () ->
                user1.reclamarBonusDiario(diaryBonus)
        );
    }

    @Test
    void alCalcularRachaConFechaActualAnteriorAUltimoReclamoLanzaFechaReclamoInvalidaException() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);
        LocalDate ayer = LocalDate.of(2026, 10, 21);

        User user1 = getUsuarioConRacha("pepito", 1, hoy);

        diaryBonus = spy(diaryBonus);
        doReturn(ayer).when(diaryBonus).getFechaActual();

        assertThrows(FechaReclamoBonusLoginInvalidaException.class, () ->
                diaryBonus.verificarPuedeReclamarBonus(user1)
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

        diaryBonus = spy(diaryBonus);
        doReturn(hoy).when(diaryBonus).getFechaActual();

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
