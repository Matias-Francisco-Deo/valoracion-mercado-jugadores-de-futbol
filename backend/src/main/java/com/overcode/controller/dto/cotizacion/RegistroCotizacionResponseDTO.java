package com.overcode.controller.dto.cotizacion;

import com.overcode.controller.dto.player.PlayerResponseDTO;
import com.overcode.model.cotizacion.registro.RegistroCotizacion;

import java.time.LocalDateTime;

public record RegistroCotizacionResponseDTO(
        Long id,
        PlayerResponseDTO player,
        Double cotizacion,
        LocalDateTime createdAt,
        EstrategiaCotizacionResponseDTO estrategiaCotizacionUsada

        ) {

    public static RegistroCotizacionResponseDTO desdeModelo(RegistroCotizacion registroCotizacion) {

        return new RegistroCotizacionResponseDTO(
                registroCotizacion.getId(),
                PlayerResponseDTO.desdeModelo(registroCotizacion.getPlayer()),
                registroCotizacion.getCotizacion(),
                registroCotizacion.getCreatedAt(),
                EstrategiaCotizacionResponseDTO.desdeModelo(registroCotizacion.getEstrategiaCotizacionUsada())

        );
    }
}
