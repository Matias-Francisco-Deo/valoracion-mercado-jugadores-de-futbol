package com.overcode.model.cotizacion;

import com.overcode.persistence.dto.jpa.cotizacion.EstrategiaCotizacionJPADTO;
import com.overcode.persistence.dto.jpa.cotizacion.EstrategiaCotizacionUnoJPADTO;
import com.overcode.persistence.dto.jpa.cotizacion.MapEstrategiaAJPADTO;


public class EstrategiaCotizacionUnoMapAJPADTO extends MapEstrategiaAJPADTO {

    public EstrategiaCotizacionUnoMapAJPADTO() {
        super(EstrategiaCotizacionUno.class);
    }

    @Override
    public EstrategiaCotizacionJPADTO getDTO(EstrategiaCotizacion estrategiaCotizacion) {
        return new EstrategiaCotizacionUnoJPADTO();
    }
}
