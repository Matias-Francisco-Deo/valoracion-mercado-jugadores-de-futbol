package com.overcode.model.bonus;

import com.overcode.model.bonus.exception.DiaDeLaSemanaInvalidoException;
import com.overcode.model.bonus.exception.DiaInvalidoException;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DiaDeLaSemanaTest {

    @Test
    void dadaUnNumeroDiaValidoEntre1Y7RetornaElDiaDeLaSemanaCorrespondiente() {
        assertEquals(DiaDeLaSemana.LUNES, DiaDeLaSemana.desdeNumeroDia(1));
        assertEquals(DiaDeLaSemana.MARTES, DiaDeLaSemana.desdeNumeroDia(2));
        assertEquals(DiaDeLaSemana.MIERCOLES, DiaDeLaSemana.desdeNumeroDia(3));
        assertEquals(DiaDeLaSemana.JUEVES, DiaDeLaSemana.desdeNumeroDia(4));
        assertEquals(DiaDeLaSemana.VIERNES, DiaDeLaSemana.desdeNumeroDia(5));
        assertEquals(DiaDeLaSemana.SABADO, DiaDeLaSemana.desdeNumeroDia(6));
        assertEquals(DiaDeLaSemana.DOMINGO, DiaDeLaSemana.desdeNumeroDia(7));
    }

    @Test
    void dadaUnNumeroDiaMenorA1LanzaDiaInvalidaException() {
        assertThrows(DiaInvalidoException.class, () -> DiaDeLaSemana.desdeNumeroDia(0));
        assertThrows(DiaInvalidoException.class, () -> DiaDeLaSemana.desdeNumeroDia(-5));
    }

    @Test
    void dadaUnNumeroDiaMayorA7LanzaDiaInvalidaException() {
        assertThrows(DiaInvalidoException.class, () -> DiaDeLaSemana.desdeNumeroDia(8));
        assertThrows(DiaInvalidoException.class, () -> DiaDeLaSemana.desdeNumeroDia(100));
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
    void cadaDiaDeLaSemanaTieneElNumeroDeDiaEsperado() {
        assertEquals(1, DiaDeLaSemana.LUNES.getNumeroDia());
        assertEquals(2, DiaDeLaSemana.MARTES.getNumeroDia());
        assertEquals(3, DiaDeLaSemana.MIERCOLES.getNumeroDia());
        assertEquals(4, DiaDeLaSemana.JUEVES.getNumeroDia());
        assertEquals(5, DiaDeLaSemana.VIERNES.getNumeroDia());
        assertEquals(6, DiaDeLaSemana.SABADO.getNumeroDia());
        assertEquals(7, DiaDeLaSemana.DOMINGO.getNumeroDia());
    }
}
