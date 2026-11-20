package com.overcode.controller.dto.metrics;

import com.overcode.controller.exception.RequestNoValidaException;
import com.overcode.model.Reloj;
import com.overcode.model.metricas.MonthlySalesPetition;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

class MonthlySalesRequestDTOTest {

    @Test
    void cuandoAnioEsMenorOIgualACero_lanzaRequestNoValidaException() {
        MonthlySalesRequestDTO dtoCero = new MonthlySalesRequestDTO(0, 11);
        RequestNoValidaException exCero = assertThrows(RequestNoValidaException.class, dtoCero::aModelo);
        assertEquals("El año debe ser un número positivo mayor a 0", exCero.getMessage());

        MonthlySalesRequestDTO dtoNegativo = new MonthlySalesRequestDTO(-1, 11);
        RequestNoValidaException exNegativo = assertThrows(RequestNoValidaException.class, dtoNegativo::aModelo);
        assertEquals("El año debe ser un número positivo mayor a 0", exNegativo.getMessage());
    }

    @Test
    void cuandoMesEsMenorAUno_lanzaRequestNoValidaException() {
        MonthlySalesRequestDTO dtoCero = new MonthlySalesRequestDTO(2026, 0);
        RequestNoValidaException exCero = assertThrows(RequestNoValidaException.class, dtoCero::aModelo);
        assertEquals("El mes debe estar comprendido entre 1 y 12", exCero.getMessage());

        MonthlySalesRequestDTO dtoNegativo = new MonthlySalesRequestDTO(2026, -5);
        RequestNoValidaException exNegativo = assertThrows(RequestNoValidaException.class, dtoNegativo::aModelo);
        assertEquals("El mes debe estar comprendido entre 1 y 12", exNegativo.getMessage());
    }

    @Test
    void cuandoMesEsMayorADoce_lanzaRequestNoValidaException() {
        MonthlySalesRequestDTO dtoTrece = new MonthlySalesRequestDTO(2026, 13);
        RequestNoValidaException ex = assertThrows(RequestNoValidaException.class, dtoTrece::aModelo);
        assertEquals("El mes debe estar comprendido entre 1 y 12", ex.getMessage());
    }

    @Test
    void cuandoParametrosSonValidos_retornaMonthlySalesPetitionConMismosValores() {
        MonthlySalesRequestDTO dto = new MonthlySalesRequestDTO(2026, 11);

        MonthlySalesPetition petition = dto.aModelo();

        assertNotNull(petition);
        assertEquals(2026, petition.getYear());
        assertEquals(11, petition.getMonth());
    }

    @Test
    void cuandoAnioEsNulo_utilizaAnioActualDeReloj() {
        LocalDate fechaMock = LocalDate.of(2025, 4, 10);
        try (MockedStatic<Reloj> reloj = mockStatic(Reloj.class)) {
            reloj.when(Reloj::getLocalDate).thenReturn(fechaMock);

            MonthlySalesRequestDTO dto = new MonthlySalesRequestDTO(null, 8);
            MonthlySalesPetition petition = dto.aModelo();

            assertEquals(2025, petition.getYear());
            assertEquals(8, petition.getMonth());
        }
    }

    @Test
    void cuandoMesEsNulo_utilizaMesActualDeReloj() {
        LocalDate fechaMock = LocalDate.of(2025, 4, 10);
        try (MockedStatic<Reloj> reloj = mockStatic(Reloj.class)) {
            reloj.when(Reloj::getLocalDate).thenReturn(fechaMock);

            MonthlySalesRequestDTO dto = new MonthlySalesRequestDTO(2024, null);
            MonthlySalesPetition petition = dto.aModelo();

            assertEquals(2024, petition.getYear());
            assertEquals(4, petition.getMonth());
        }
    }

    @Test
    void cuandoAmbosParametrosSonNulos_utilizaAnioYMesActualesDeReloj() {
        LocalDate fechaMock = LocalDate.of(2025, 9, 20);
        try (MockedStatic<Reloj> reloj = mockStatic(Reloj.class)) {
            reloj.when(Reloj::getLocalDate).thenReturn(fechaMock);

            MonthlySalesRequestDTO dto = new MonthlySalesRequestDTO(null, null);
            MonthlySalesPetition petition = dto.aModelo();

            assertEquals(2025, petition.getYear());
            assertEquals(9, petition.getMonth());
        }
    }
}
