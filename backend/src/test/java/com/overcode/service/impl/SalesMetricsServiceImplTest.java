package com.overcode.service.impl;

import com.overcode.model.metricas.MonthlySalesPetition;
import com.overcode.persistence.repository.interfaces.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // TODO NO MOCKEAR SERVICE AAAAAAA
class SalesMetricsServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    private SalesMetricsServiceImpl salesMetricsService;

    @BeforeEach
    void setUp() {
        salesMetricsService = new SalesMetricsServiceImpl(transactionRepository);
    }

    private static MonthlySalesPetition crearPeticion( Integer year, Integer month) {
        return new MonthlySalesPetition(year, month);
    }

    @Test
    void conAnioYMesValidos_calculaLimitesYConsultaRepositorio() {
        ArgumentCaptor<LocalDateTime> inicioCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> finCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        when(transactionRepository.contarVentasEntre(any(), any())).thenReturn(10L);

        Long totalVentas = salesMetricsService.obtenerCantidadVentasMensuales(crearPeticion(2026, 11));

        assertEquals(10L, totalVentas);
        verify(transactionRepository).contarVentasEntre(inicioCaptor.capture(), finCaptor.capture());
        assertEquals(LocalDateTime.of(2026, 11, 1, 0, 0, 0), inicioCaptor.getValue());
        assertEquals(2026, finCaptor.getValue().getYear());
        assertEquals(11, finCaptor.getValue().getMonthValue());
        assertEquals(30, finCaptor.getValue().getDayOfMonth());
    }



//    @Test
//    void cuandoMesEsMenorAUno_lanzaValidationException() { // TODO este test sería del request en todo caso
//        assertThrows(ValidationException.class, () ->
//                salesMetricsService.obtenerCantidadVentasMensuales(crearPeticion(2026, -1))
//        );
//    }
//
//    @Test
//    void cuandoMesEsMayorADoce_lanzaValidationException() {
//        assertThrows(ValidationException.class, () ->
//                salesMetricsService.obtenerCantidadVentasMensuales(crearPeticion(2026, 13))
//        );
//    }

//    @Test
//    void cuandoAnioEsMenorOIgualACero_lanzaValidationException() {
//        assertThrows(ValidationException.class, () ->
//                salesMetricsService.obtenerCantidadVentasMensuales(0)
//        );
//    }

//    @Test
//    void cuandoParametrosSonNulos_utilizaMesYAnioActuales() {
//        ArgumentCaptor<LocalDateTime> inicioCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
//        ArgumentCaptor<LocalDateTime> finCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
//        when(transactionRepository.contarVentasEntre(any(), any())).thenReturn(5L);
//
//        Long totalVentas = salesMetricsService.obtenerCantidadVentasMensuales(null);
//
//        assertEquals(5L, totalVentas);
//        verify(transactionRepository).contarVentasEntre(inicioCaptor.capture(), finCaptor.capture());
//        LocalDate hoy = LocalDate.now();
//        assertEquals(hoy.getYear(), inicioCaptor.getValue().getYear());
//        assertEquals(hoy.getMonthValue(), inicioCaptor.getValue().getMonthValue());
//        assertEquals(1, inicioCaptor.getValue().getDayOfMonth());
//    }

    @Test
    void enFebreroBisiesto_limiteFinEsVeintinueveDeFebrero() {
        ArgumentCaptor<LocalDateTime> finCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        when(transactionRepository.contarVentasEntre(any(), any())).thenReturn(2L);

        salesMetricsService.obtenerCantidadVentasMensuales(crearPeticion(2024, 2));

        verify(transactionRepository).contarVentasEntre(any(), finCaptor.capture());
        assertEquals(29, finCaptor.getValue().getDayOfMonth());
    }

}
