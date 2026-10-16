package com.overcode.persistence.dto.jpa.cotizacion;

import com.overcode.model.cotizacion.EstrategiaCotizacion;
import com.overcode.model.cotizacion.EstrategiaCotizacionUnoMapAJPADTO;
import com.overcode.service.exception.EntidadNoEncontradaException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public enum TipoEstrategiaCotizacion {;

    private static final List<MapEstrategiaAJPADTO> maps = new ArrayList<>(List.of(
            new EstrategiaCotizacionUnoMapAJPADTO()
    ));

    public static EstrategiaCotizacionJPADTO getValueOf(EstrategiaCotizacion estrategiaCotizacion) {

        Class<? extends EstrategiaCotizacion> estrategiaClase = estrategiaCotizacion.getClass();

        Optional<MapEstrategiaAJPADTO> mapEstrategia = maps.stream().filter(
                map -> map.esAplicable(estrategiaClase)
        ).findFirst();

        if (mapEstrategia.isEmpty()) {
            throw new EntidadNoEncontradaException("Unexpected value: " + estrategiaClase.getSimpleName());
        }

        return mapEstrategia.get().getDTO(estrategiaCotizacion);
    }

    public static void agregarMapEstrategia(MapEstrategiaAJPADTO estrategiaADTO) {
        maps.add(estrategiaADTO);
    }

    public static void eliminarMapEstrategia(MapEstrategiaAJPADTO estrategiaADTO) {
        maps.remove(estrategiaADTO);
    }
}
