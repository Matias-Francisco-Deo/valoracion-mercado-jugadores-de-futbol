package com.overcode.model.cotizacion;

import com.overcode.persistence.dto.jpa.cotizacion.EstrategiaCotizacionJPADTO;
import com.overcode.persistence.dto.jpa.cotizacion.EstrategiaCotizacionUnoJPADTO;
import com.overcode.persistence.dto.jpa.cotizacion.MapEstrategiaADTO;


public class EstrategiaCotizacionUnoMapADTO extends MapEstrategiaADTO {

    public EstrategiaCotizacionUnoMapADTO() {
        super(EstrategiaCotizacionUno.class);
    }

    @Override
    public EstrategiaCotizacionJPADTO getDTO(Class<? extends EstrategiaCotizacion> estrategiaClase) {
        return new EstrategiaCotizacionUnoJPADTO();
    }
}
