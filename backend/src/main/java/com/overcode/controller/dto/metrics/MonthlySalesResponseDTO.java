package com.overcode.controller.dto.metrics;

public record MonthlySalesResponseDTO(
        Integer year,
        Integer month,
        Long totalSales
) {
}
