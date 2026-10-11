package com.overcode.controller.dto.cotizacion;

import com.overcode.model.cotizacion.EstrategiaCotizacion;

public record EstrategiaCotizacionResponseDTO(
        // TODO un comentario sobre qué hace el algoritmo? muy innecesario? tal vez el tipo? el nombre de la clase? Seh, god Título
        Long id,
        Double valorBase,
        Double factorEscala
) {

    public static EstrategiaCotizacionResponseDTO desdeModelo(EstrategiaCotizacion modelo) {
        return new EstrategiaCotizacionResponseDTO(
                modelo.getId(),
                modelo.getValorBase(),
                modelo.getFactorEscala()
        );
    }
}
