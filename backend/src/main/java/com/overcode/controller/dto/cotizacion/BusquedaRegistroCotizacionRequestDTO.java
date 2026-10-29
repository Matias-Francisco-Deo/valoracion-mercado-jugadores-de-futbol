package com.overcode.controller.dto.cotizacion;

import com.overcode.controller.exception.RequestNoValidaException;
import com.overcode.model.cotizacion.registro.BusquedaRegistroCotizacion;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record BusquedaRegistroCotizacionRequestDTO(
        @NotNull(message = "El ID del jugador no puede ser null")
        Long playerId,
        LocalDateTime startDate,
        LocalDateTime endDate) {
    public BusquedaRegistroCotizacion aModelo() {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new RequestNoValidaException("La fecha de inicio no puede ser posterior a la fecha de fin");

        }
        return new BusquedaRegistroCotizacion(playerId,
                startDate,
                endDate
        );
    }
}

