package com.overcode.persistence.dto.jpa.cotizacion;

import com.overcode.model.cotizacion.EstrategiaCotizacion;
import com.overcode.model.cotizacion.EstrategiaCotizacionUnoMapADTO;
import com.overcode.service.exception.EntidadNoEncontradaException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public enum TipoEstrategiaCotizacion {;

    private static final List<MapEstrategiaADTO> maps = new ArrayList<>(List.of(
            new EstrategiaCotizacionUnoMapADTO()
    ));

    public static EstrategiaCotizacionJPADTO getValueOf(Class<? extends EstrategiaCotizacion> estrategiaClase) {

        Optional<MapEstrategiaADTO> mapEstrategia = maps.stream().filter(
                map -> map.esAplicable(estrategiaClase)
        ).findFirst();

        if (mapEstrategia.isEmpty()) {
            throw new EntidadNoEncontradaException("Unexpected value: " + estrategiaClase.getSimpleName());
        }

        return mapEstrategia.get().getDTO(estrategiaClase);
    }

    public static void agregarMapEstrategia(MapEstrategiaADTO estrategiaADTO) {
        maps.add(estrategiaADTO);
    }

    public static void eliminarMapEstrategia(MapEstrategiaADTO estrategiaADTO) {
        maps.remove(estrategiaADTO);
    }
}
