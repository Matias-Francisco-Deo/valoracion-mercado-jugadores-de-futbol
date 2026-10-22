package com.overcode.model.bonus;

import com.overcode.model.bonus.exception.*;
import com.overcode.testUtils.TestLoginBonusUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(1.0, loginBonus.getBonusParaRacha(1));
        assertEquals(2.0, loginBonus.getBonusParaRacha(2));
        assertEquals(2.0, loginBonus.getBonusParaRacha(3));
        assertEquals(5.0, loginBonus.getBonusParaRacha(4));
        assertEquals(5.0, loginBonus.getBonusParaRacha(5));
        assertEquals(10.0, loginBonus.getBonusParaRacha(6));
        assertEquals(15.0, loginBonus.getBonusParaRacha(7));
    }

    @Test
    void alCalcularSiguienteRachaSinReclamoPrevioRetorna1() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);
        int siguienteRacha = loginBonus.calcularSiguienteRacha(null, hoy, 0);
        assertEquals(1, siguienteRacha);
    }

    @Test
    void alCalcularSiguienteRachaEnDiasConsecutivosSeIncrementaLaRacha() {
        LocalDate ayer = LocalDate.of(2026, 10, 21);
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        assertEquals(2, loginBonus.calcularSiguienteRacha(ayer, hoy, 1));
        assertEquals(3, loginBonus.calcularSiguienteRacha(ayer, hoy, 2));
        assertEquals(7, loginBonus.calcularSiguienteRacha(ayer, hoy, 6));
    }

    @Test
    void alPasarMasDeUnDiaSinReclamarSeReiniciaLaRachaADia1() {
        LocalDate haceTresDias = LocalDate.of(2026, 10, 19);
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        int siguienteRacha = loginBonus.calcularSiguienteRacha(haceTresDias, hoy, 4);
        assertEquals(1, siguienteRacha);
    }

    @Test
    void trasInactividadProlongadaSeReiniciaLaRachaADia1SinErrores() {
        LocalDate haceUnMes = LocalDate.of(2026, 9, 22);
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        int siguienteRacha = loginBonus.calcularSiguienteRacha(haceUnMes, hoy, 5);
        assertEquals(1, siguienteRacha);
    }

    @Test
    void alReclamarEnElDia8LaRachaSeReiniciaADia1CicloSemanal() {
        LocalDate ayer = LocalDate.of(2026, 10, 21);
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        int siguienteRacha = loginBonus.calcularSiguienteRacha(ayer, hoy, 7);
        assertEquals(1, siguienteRacha);
    }

    @Test
    void alIntentarReclamarDosVecesElMismoDiaLanzaBonusYaReclamadoHoyException() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        assertThrows(BonusYaReclamadoHoyException.class, () ->
                loginBonus.calcularSiguienteRacha(hoy, hoy, 1)
        );
    }

    @Test
    void alCalcularRachaConFechaActualAnteriorAUltimoReclamoLanzaFechaReclamoInvalidaException() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);
        LocalDate ayer = LocalDate.of(2026, 10, 21);

        assertThrows(FechaReclamoInvalidaException.class, () ->
                loginBonus.calcularSiguienteRacha(hoy, ayer, 1)
        );
    }

    @Test
    void alCalcularRachaConFechaActualNulaLanzaFechaReclamoInvalidaException() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        assertThrows(FechaReclamoInvalidaException.class, () ->
                loginBonus.calcularSiguienteRacha(hoy, null, 1)
        );
    }

    @Test
    void puedeReclamarHoyRetornaTrueSiNoHayReclamoPrevio() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);
        assertTrue(loginBonus.puedeReclamarHoy(null, hoy));
    }

    @Test
    void puedeReclamarHoyRetornaFalseSiYaReclamoHoy() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);
        assertFalse(loginBonus.puedeReclamarHoy(hoy, hoy));
    }

    @Test
    void puedeReclamarHoyRetornaTrueSiReclamoAyer() {
        LocalDate ayer = LocalDate.of(2026, 10, 21);
        LocalDate hoy = LocalDate.of(2026, 10, 22);
        assertTrue(loginBonus.puedeReclamarHoy(ayer, hoy));
    }

    @Test
    void alCrearLoginBonusConMapaIncompletoLanzaLoginBonusInvalidoException() {
        Map<DiaDeLaSemana, Double> mapaIncompleto = new EnumMap<>(DiaDeLaSemana.class);
        mapaIncompleto.put(DiaDeLaSemana.LUNES, 1.0);

        assertThrows(LoginBonusInvalidoException.class, () ->
                new LoginBonus("Incompleto", mapaIncompleto)
        );
    }

    @Test
    void alCrearLoginBonusConValorNuloONegativoLanzaValorBonusInvalidoException() {
        Map<DiaDeLaSemana, Double> mapaConCero = new EnumMap<>(DiaDeLaSemana.class);
        for (DiaDeLaSemana dia : DiaDeLaSemana.values()) {
            mapaConCero.put(dia, 1.0);
        }
        mapaConCero.put(DiaDeLaSemana.MIERCOLES, 0.0);

        assertThrows(ValorBonusInvalidoException.class, () ->
                new LoginBonus("Con Cero", mapaConCero)
        );

        mapaConCero.put(DiaDeLaSemana.MIERCOLES, -2.0);
        assertThrows(ValorBonusInvalidoException.class, () ->
                new LoginBonus("Con Negativo", mapaConCero)
        );

        mapaConCero.put(DiaDeLaSemana.MIERCOLES, null);
        assertThrows(ValorBonusInvalidoException.class, () ->
                new LoginBonus("Con Null", mapaConCero)
        );
    }

    @Test
    void alConsultarBonusParaDiaNuloLanzaDiaDeLaSemanaInvalidoException() {
        assertThrows(DiaDeLaSemanaInvalidoException.class, () -> loginBonus.getBonusParaDia(null));
    }
}
