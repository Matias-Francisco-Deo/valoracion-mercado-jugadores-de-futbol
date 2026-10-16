package com.overcode.controller.dto.cotizacion;

import com.overcode.model.cotizacion.registro.BusquedaRegistroCotizacion;

import java.time.LocalDateTime;

public record BusquedaRegistroCotizacionRequestDTO(Long playerId, LocalDateTime startDate, LocalDateTime endDate) {
    public BusquedaRegistroCotizacion aModelo() {
        return new BusquedaRegistroCotizacion(playerId,
                startDate == null ? LocalDateTime.MIN : startDate,
                endDate == null ? LocalDateTime.MAX : endDate
        );
    }
}

