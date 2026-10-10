package com.overcode.testUtils.cotizacion;

import com.overcode.persistence.dto.jpa.cotizacion.EstrategiaCotizacionJPADTO;
import com.overcode.persistence.dto.jpa.cotizacion.MapEstrategiaADTO;


public class EstrategiaCotizacionSiempre2MapADTO extends MapEstrategiaADTO {

    public EstrategiaCotizacionSiempre2MapADTO() {
        super(EstrategiaCotizacionSiempre2.class);
    }

    @Override
    public EstrategiaCotizacionJPADTO getDTO() {
        return new EstrategiaCotizacionSiempre2JPADTO();
    }
}
