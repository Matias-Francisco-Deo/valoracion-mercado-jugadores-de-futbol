package com.overcode.persistence.dto.jpa.cotizacion;

import com.overcode.model.cotizacion.EstrategiaCotizacion;


public abstract class MapEstrategiaAJPADTO {
    private final Class<? extends EstrategiaCotizacion> estrategia;

    protected MapEstrategiaAJPADTO(Class<? extends EstrategiaCotizacion> estrategia) {
        this.estrategia = estrategia;
    }

    public boolean esAplicable(Class<? extends EstrategiaCotizacion> estrategiaClase) {
        return estrategiaClase == estrategia;
    }

    public abstract EstrategiaCotizacionJPADTO getDTO(EstrategiaCotizacion estrategiaCotizacion);
}
