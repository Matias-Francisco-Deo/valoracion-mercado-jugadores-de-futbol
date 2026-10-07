package com.overcode.persistence.dto.jpa.cotizacion;

import com.overcode.model.cotizacion.EstrategiaCotizacion;

public enum TipoEstrategiaCotizacion {
    ESTRATEGIA_1 {
        @Override
        public EstrategiaCotizacionJPADTO instanciarDto(EstrategiaCotizacion estrategiaCotizacion) {
            // Implementation for Estrategia 1
            return null;
        }
    },
    ESTRATEGIA_2 {
        @Override
        public EstrategiaCotizacionJPADTO instanciarDto(EstrategiaCotizacion estrategiaCotizacion) {
            // Implementation for Estrategia 2
            return null;
        }
    };

    public static EstrategiaCotizacionJPADTO getValueOf(Class<? extends EstrategiaCotizacion> estrategiaClase) {
        throw switch (estrategiaClase) {
//            case Angel ignored -> ANGEL;
//            case Demonio ignored -> DEMONIO;
            default -> new IllegalStateException("Unexpected value: " + estrategiaClase.getSimpleName());
        };
    }

    public abstract EstrategiaCotizacionJPADTO instanciarDto(EstrategiaCotizacion estrategiaCotizacion);
}
