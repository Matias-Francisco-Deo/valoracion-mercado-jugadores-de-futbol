package com.overcode.testUtils.cotizacion.siempre2;

import com.overcode.model.cotizacion.EstrategiaCotizacion;
import com.overcode.persistence.dto.jpa.cotizacion.EstrategiaCotizacionJPADTO;
import com.overcode.persistence.dto.jpa.cotizacion.mapper.MapEstrategiaAJPADTO;


public class EstrategiaCotizacionSiempre2MapAJPADTO extends MapEstrategiaAJPADTO {

    public EstrategiaCotizacionSiempre2MapAJPADTO() {
        super(EstrategiaCotizacionSiempre2.class);
    }

    @Override
    public EstrategiaCotizacionJPADTO getDTO(EstrategiaCotizacion estrategiaCotizacion) {
        return new EstrategiaCotizacionSiempre2JPADTO();
    }
}
