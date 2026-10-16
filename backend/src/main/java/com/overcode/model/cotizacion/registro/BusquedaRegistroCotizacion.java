package com.overcode.model.cotizacion.registro;

import java.time.LocalDateTime;

public record BusquedaRegistroCotizacion(
        Long playerId,
        LocalDateTime inicio,
        LocalDateTime fin
){}
