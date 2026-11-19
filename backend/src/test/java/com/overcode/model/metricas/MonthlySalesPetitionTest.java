package com.overcode.model.metricas;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MonthlySalesPetitionTest {

    @Test
    void getMinTime_retornaInicioDelPrimerDiaDelMes() {
        MonthlySalesPetition petition = new MonthlySalesPetition(2026, 11);

        assertEquals(LocalDateTime.of(2026, 11, 1, 0, 0, 0), petition.getMinTime());
    }

    @Test
    void getMaxTime_retornaFinDelUltimoDiaDelMes() {
        MonthlySalesPetition petition = new MonthlySalesPetition(2026, 11);

        assertEquals(30, petition.getMaxTime().getDayOfMonth());
        assertEquals(YearMonth.of(2026, 11).atEndOfMonth().atTime(LocalTime.MAX), petition.getMaxTime());
    }

    @Test
    void getMaxTime_enAnioBisiesto_febreroTiene29Dias() {
        MonthlySalesPetition petition = new MonthlySalesPetition(2024, 2);

        assertEquals(29, petition.getMaxTime().getDayOfMonth());
        assertEquals(YearMonth.of(2024, 2).atEndOfMonth().atTime(LocalTime.MAX), petition.getMaxTime());
    }

    @Test
    void getMaxTime_enAnioNoBisiesto_febreroTiene28Dias() {
        MonthlySalesPetition petition = new MonthlySalesPetition(2023, 2);

        assertEquals(28, petition.getMaxTime().getDayOfMonth());
        assertEquals(YearMonth.of(2023, 2).atEndOfMonth().atTime(LocalTime.MAX), petition.getMaxTime());
    }

    @Test
    void getMaxTime_enMesCon31Dias_retornaFinDelDia31() {
        MonthlySalesPetition petition = new MonthlySalesPetition(2026, 1);

        assertEquals(31, petition.getMaxTime().getDayOfMonth());
        assertEquals(YearMonth.of(2026, 1).atEndOfMonth().atTime(LocalTime.MAX), petition.getMaxTime());
    }

    @Test
    void getYearYGetMonth_retornanValoresCorrectos() {
        MonthlySalesPetition petition = new MonthlySalesPetition(2026, 11);

        assertEquals(2026, petition.getYear());
        assertEquals(11, petition.getMonth());
    }
}
