package com.overcode.model.bonus;

import com.overcode.model.bonus.exception.DiaDeLaSemanaInvalidoException;
import com.overcode.model.bonus.exception.RachaInvalidaException;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;

import static org.junit.jupiter.api.Assertions.*;

class DiaDeLaSemanaTest {

    @Test
    void dadaUnaRachaValidaEntre1Y7RetornaElDiaDeLaSemanaCorrespondiente() {
        assertEquals(DiaDeLaSemana.LUNES, DiaDeLaSemana.desdeRacha(1));
        assertEquals(DiaDeLaSemana.MARTES, DiaDeLaSemana.desdeRacha(2));
        assertEquals(DiaDeLaSemana.MIERCOLES, DiaDeLaSemana.desdeRacha(3));
        assertEquals(DiaDeLaSemana.JUEVES, DiaDeLaSemana.desdeRacha(4));
        assertEquals(DiaDeLaSemana.VIERNES, DiaDeLaSemana.desdeRacha(5));
        assertEquals(DiaDeLaSemana.SABADO, DiaDeLaSemana.desdeRacha(6));
        assertEquals(DiaDeLaSemana.DOMINGO, DiaDeLaSemana.desdeRacha(7));
    }

    @Test
    void dadaUnaRachaMenorA1LanzaRachaInvalidaException() {
        assertThrows(RachaInvalidaException.class, () -> DiaDeLaSemana.desdeRacha(0));
        assertThrows(RachaInvalidaException.class, () -> DiaDeLaSemana.desdeRacha(-5));
    }

    @Test
    void dadaUnaRachaMayorA7LanzaRachaInvalidaException() {
        assertThrows(RachaInvalidaException.class, () -> DiaDeLaSemana.desdeRacha(8));
        assertThrows(RachaInvalidaException.class, () -> DiaDeLaSemana.desdeRacha(100));
    }

    @Test
    void dadoUnDayOfWeekValidoRetornaElDiaDeLaSemanaCorrespondiente() {
        assertEquals(DiaDeLaSemana.LUNES, DiaDeLaSemana.desdeDayOfWeek(DayOfWeek.MONDAY));
        assertEquals(DiaDeLaSemana.MARTES, DiaDeLaSemana.desdeDayOfWeek(DayOfWeek.TUESDAY));
        assertEquals(DiaDeLaSemana.MIERCOLES, DiaDeLaSemana.desdeDayOfWeek(DayOfWeek.WEDNESDAY));
        assertEquals(DiaDeLaSemana.JUEVES, DiaDeLaSemana.desdeDayOfWeek(DayOfWeek.THURSDAY));
        assertEquals(DiaDeLaSemana.VIERNES, DiaDeLaSemana.desdeDayOfWeek(DayOfWeek.FRIDAY));
        assertEquals(DiaDeLaSemana.SABADO, DiaDeLaSemana.desdeDayOfWeek(DayOfWeek.SATURDAY));
        assertEquals(DiaDeLaSemana.DOMINGO, DiaDeLaSemana.desdeDayOfWeek(DayOfWeek.SUNDAY));
    }

    @Test
    void dadoUnDayOfWeekNuloLanzaDiaDeLaSemanaInvalidoException() {
        assertThrows(DiaDeLaSemanaInvalidoException.class, () -> DiaDeLaSemana.desdeDayOfWeek(null));
    }

    @Test
    void cadaDiaDeLaSemanaTieneElNumeroDeRachaEsperado() {
        assertEquals(1, DiaDeLaSemana.LUNES.getNumeroRacha());
        assertEquals(2, DiaDeLaSemana.MARTES.getNumeroRacha());
        assertEquals(3, DiaDeLaSemana.MIERCOLES.getNumeroRacha());
        assertEquals(4, DiaDeLaSemana.JUEVES.getNumeroRacha());
        assertEquals(5, DiaDeLaSemana.VIERNES.getNumeroRacha());
        assertEquals(6, DiaDeLaSemana.SABADO.getNumeroRacha());
        assertEquals(7, DiaDeLaSemana.DOMINGO.getNumeroRacha());
    }
}
