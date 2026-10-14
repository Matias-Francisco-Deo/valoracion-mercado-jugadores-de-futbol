package com.overcode.controller.dto.cotizacion;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ActualizarFactorEscalaRequestDTO(
        @NotNull(message = "El factor escala no puede ser nulo")
        @Positive(message = "El factor escala debe ser mayor a 0")
        Double factorEscala
) {
}
