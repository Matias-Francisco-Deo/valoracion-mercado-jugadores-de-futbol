package com.overcode.controller.dto.metrics;

import com.overcode.controller.exception.RequestNoValidaException;
import com.overcode.model.Reloj;
import com.overcode.model.metricas.MonthlySalesPetition;

public record MonthlySalesRequestDTO(
        Integer year,
        Integer month
) {
    public MonthlySalesPetition aModelo() {

        int notNullYear = year != null ? year : Reloj.getLocalDate().getYear();
        int notNullMonth = month != null ? month : Reloj.getLocalDate().getMonthValue();

        if (notNullYear <= 0) {
            throw new RequestNoValidaException("El año debe ser un número positivo mayor a 0");
        }
        if (notNullMonth < 1 || notNullMonth > 12) {
            throw new RequestNoValidaException("El mes debe estar comprendido entre 1 y 12");
        }

        return new MonthlySalesPetition(notNullYear, notNullMonth);
    }
}
