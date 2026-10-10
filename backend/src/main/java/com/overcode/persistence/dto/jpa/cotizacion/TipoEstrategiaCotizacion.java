package com.overcode.persistence.dto.jpa.cotizacion;

import com.overcode.model.cotizacion.EstrategiaCotizacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public enum TipoEstrategiaCotizacion {;
//    ESTRATEGIA_1 {
//        @Override
//        public EstrategiaCotizacionJPADTO instanciarDto(EstrategiaCotizacion estrategiaCotizacion) {
//            // Implementation for Estrategia 1
//            return null;
//        }
//    },
//    ESTRATEGIA_2 {
//        @Override
//        public EstrategiaCotizacionJPADTO instanciarDto(EstrategiaCotizacion estrategiaCotizacion) {
//            // Implementation for Estrategia 2
//            return null;
//        }
//    };

    private static final List<MapEstrategiaADTO> maps = new ArrayList<>(); // TODO sirve?

    public static EstrategiaCotizacionJPADTO getValueOf(Class<? extends EstrategiaCotizacion> estrategiaClase) {

        Optional<MapEstrategiaADTO> mapEstrategia = maps.stream().filter(
                map -> map.esAplicable(estrategiaClase)
        ).findFirst();

        if (mapEstrategia.isEmpty()) {
            throw new IllegalStateException("Unexpected value: " + estrategiaClase.getSimpleName());
        }

        return mapEstrategia.get().getDTO();
    }

    public static void agregarMapEstrategia(MapEstrategiaADTO estrategiaADTO) {
        maps.add(estrategiaADTO);
    }
}
