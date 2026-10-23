package com.overcode.model.bonus;

import com.overcode.model.User;
import com.overcode.model.bonus.exception.*;
import com.overcode.testUtils.TestLoginBonusUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;

import static com.overcode.testUtils.TestUserUtil.getUsuarioConRacha;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class LoginBonusTest {

    private LoginBonus loginBonus;

    @BeforeEach
    void setUp() {
        loginBonus = TestLoginBonusUtil.getLoginBonusBase();
    }

    @Test
    void alConsultarBonusParaCadaDiaRetornaElValorConfigurado() {
        assertEquals(1.0, loginBonus.getBonusParaDia(DiaDeLaSemana.LUNES));
        assertEquals(2.0, loginBonus.getBonusParaDia(DiaDeLaSemana.MARTES));
        assertEquals(2.0, loginBonus.getBonusParaDia(DiaDeLaSemana.MIERCOLES));
        assertEquals(5.0, loginBonus.getBonusParaDia(DiaDeLaSemana.JUEVES));
        assertEquals(5.0, loginBonus.getBonusParaDia(DiaDeLaSemana.VIERNES));
        assertEquals(10.0, loginBonus.getBonusParaDia(DiaDeLaSemana.SABADO));
        assertEquals(15.0, loginBonus.getBonusParaDia(DiaDeLaSemana.DOMINGO));
    }

    @Test
    void alConsultarBonusParaRachaRetornaElValorCorrespondiente() {
        User user = getUsuarioConRacha("pepito", 1, LocalDate.of(2026, 10, 22));
        loginBonus = spy(loginBonus);
        doReturn(LocalDate.of(2026, 10, 23)).when(loginBonus).getFechaActual();

        assertEquals(2.0, loginBonus.getBonusParaUser(user).givenBonus());
    }

    @Test
    void alCalcularSiguienteRachaSinReclamoPrevioRetorna1() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);
        User user = getUsuarioConRacha("pepito", 0, hoy);

        int siguienteRacha = loginBonus.calcularRacha(user);

        assertEquals(1, siguienteRacha);
    }

    @Test
    void alCalcularSiguienteRachaEnDiasConsecutivosSeIncrementaLaRacha() {
        LocalDate ayer = LocalDate.of(2026, 10, 21);
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        User user1 = getUsuarioConRacha("pepito", 1, ayer);

        loginBonus = spy(loginBonus);
        doReturn(hoy).when(loginBonus).getFechaActual();

        assertEquals(2, loginBonus.calcularRacha(user1));

        user1.setStreakCount(2);

        assertEquals(3, loginBonus.calcularRacha(user1));

        user1.setStreakCount(6);

        assertEquals(7, loginBonus.calcularRacha(user1));
    }

    @Test
    void alPasarMasDeUnDiaSinReclamarSeReiniciaLaRachaADia1() {
        LocalDate haceTresDias = LocalDate.of(2026, 10, 19);
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        User user1 = getUsuarioConRacha("pepito", 4, haceTresDias);

        loginBonus = spy(loginBonus);
        doReturn(hoy).when(loginBonus).getFechaActual();

        int siguienteRacha = loginBonus.calcularRacha(user1);
        assertEquals(1, siguienteRacha);
    }

    @Test
    void trasInactividadProlongadaSeReiniciaLaRachaADia1SinErrores() {
        LocalDate haceUnMes = LocalDate.of(2026, 9, 22);
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        User user1 = getUsuarioConRacha("pepito", 5, haceUnMes);

        loginBonus = spy(loginBonus);
        doReturn(hoy).when(loginBonus).getFechaActual();

        int siguienteRacha = loginBonus.calcularRacha(user1);
        assertEquals(1, siguienteRacha);
    }

    @Test
    void alReclamarEnElDia8LaRachaSeReiniciaADia1CicloSemanal() {
        LocalDate ayer = LocalDate.of(2026, 10, 21);
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        User user1 = getUsuarioConRacha("pepito", 7, ayer);

        loginBonus = spy(loginBonus);
        doReturn(hoy).when(loginBonus).getFechaActual();

        int siguienteRacha = loginBonus.calcularRacha(user1);
        assertEquals(1, siguienteRacha);
    }

    @Test
    void alIntentarReclamarDosVecesElMismoDiaLanzaBonusYaReclamadoHoyException() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        User user1 = getUsuarioConRacha("pepito", 1, hoy);

        loginBonus = spy(loginBonus);
        doReturn(hoy).when(loginBonus).getFechaActual();

        assertThrows(BonusYaReclamadoHoyException.class, () ->
                loginBonus.calcularRacha(user1)
        );
    }

    @Test
    void alCalcularRachaConFechaActualAnteriorAUltimoReclamoLanzaFechaReclamoInvalidaException() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);
        LocalDate ayer = LocalDate.of(2026, 10, 21);

        User user1 = getUsuarioConRacha("pepito", 1, hoy);

        loginBonus = spy(loginBonus);
        doReturn(ayer).when(loginBonus).getFechaActual();

        assertThrows(FechaReclamoInvalidaException.class, () ->
                loginBonus.calcularRacha(user1)
        );
    }


    @Test
    void yaReclamoBonusRetornaTrueSiNoHayReclamoPrevio() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        User user1 = getUsuarioConRacha("pepito", 1, null);

        assertTrue(user1.yaReclamoBonus(hoy));
    }

    @Test
    void yaReclamoBonusRetornaFalseSiYaReclamoHoy() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        User user1 = getUsuarioConRacha("pepito", 1, hoy);

        loginBonus = spy(loginBonus);
        doReturn(hoy).when(loginBonus).getFechaActual();

        assertTrue(user1.yaReclamoBonus(hoy));
    }

    @Test
    void yaReclamoBonusRetornaTrueSiReclamoAyer() {
        LocalDate ayer = LocalDate.of(2026, 10, 21);
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        User user1 = getUsuarioConRacha("pepito", 1, ayer);

        assertFalse(user1.yaReclamoBonus(hoy));
    }

    @Test
    void alCrearLoginBonusConMapaIncompletoLanzaLoginBonusInvalidoException() {
        Map<DiaDeLaSemana, Double> mapIncompleto = new EnumMap<>(DiaDeLaSemana.class);
        mapIncompleto.put(DiaDeLaSemana.LUNES, 1.0);

        assertThrows(LoginBonusInvalidoException.class, () ->
                new LoginBonus("Incompleto", mapIncompleto)
        );
    }

    @Test
    void alCrearLoginBonusConValorNuloONegativoLanzaValorBonusInvalidoException() {
        Map<DiaDeLaSemana, Double> mapConCero = new EnumMap<>(DiaDeLaSemana.class);
        for (DiaDeLaSemana dia : DiaDeLaSemana.values()) {
            mapConCero.put(dia, 1.0);
        }
        mapConCero.put(DiaDeLaSemana.MIERCOLES, 0.0);

        assertThrows(ValorBonusInvalidoException.class, () ->
                new LoginBonus("Con Cero", mapConCero)
        );

        mapConCero.put(DiaDeLaSemana.MIERCOLES, -2.0);
        assertThrows(ValorBonusInvalidoException.class, () ->
                new LoginBonus("Con Negativo", mapConCero)
        );

        mapConCero.put(DiaDeLaSemana.MIERCOLES, null);
        assertThrows(ValorBonusInvalidoException.class, () ->
                new LoginBonus("Con Null", mapConCero)
        );
    }

    @Test
    void alConsultarBonusParaDiaNuloLanzaDiaDeLaSemanaInvalidoException() {
        assertThrows(DiaDeLaSemanaInvalidoException.class, () -> loginBonus.getBonusParaDia(null));
    }
}
