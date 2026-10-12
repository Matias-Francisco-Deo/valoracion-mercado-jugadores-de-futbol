package com.overcode.controller.dto.cotizacion;

import com.overcode.model.cotizacion.EstrategiaCotizacion;

public record EstrategiaCotizacionResponseDTO(
        Long id,
        String titulo,
        Double valorBase,
        Double factorEscala
) {

    public static EstrategiaCotizacionResponseDTO desdeModelo(EstrategiaCotizacion estrategiaCotizacion) {
        return new EstrategiaCotizacionResponseDTO(
                estrategiaCotizacion.getId(),
                estrategiaCotizacion.getTitulo(),
                estrategiaCotizacion.getValorBase(),
                estrategiaCotizacion.getFactorEscala()
        );
    }
}
