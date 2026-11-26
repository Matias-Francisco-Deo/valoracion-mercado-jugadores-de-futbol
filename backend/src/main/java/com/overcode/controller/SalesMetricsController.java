package com.overcode.controller;

import com.overcode.controller.dto.metrics.MonthlySalesRequestDTO;
import com.overcode.controller.dto.metrics.MonthlySalesResponseDTO;
import com.overcode.model.metricas.MonthlySalesPetition;
import com.overcode.service.interfaces.SalesMetricsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/metrics/sales")
@Tag(name = "Métricas de Ventas", description = "Endpoints de administración para consultar métricas de ventas")
public class SalesMetricsController {

    private final SalesMetricsService salesMetricsService;

    public SalesMetricsController(SalesMetricsService salesMetricsService) {
        this.salesMetricsService = salesMetricsService;
    }

    @GetMapping
    @Operation(summary = "Obtener cantidad de ventas mensuales", description = "Retorna la cantidad de ventas ejecutadas en el mes y año indicados (por defecto el mes y año actuales).")
    public ResponseEntity<MonthlySalesResponseDTO> obtenerVentasMensuales(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        MonthlySalesRequestDTO requestDTO = new MonthlySalesRequestDTO(year, month);
        MonthlySalesPetition petition = requestDTO.aModelo();

        Long totalVentas = salesMetricsService.obtenerCantidadVentasMensuales(petition);

        MonthlySalesResponseDTO response = new MonthlySalesResponseDTO(petition.getYear(), petition.getMonth(), totalVentas);

        return ResponseEntity.ok(response);
    }
}
