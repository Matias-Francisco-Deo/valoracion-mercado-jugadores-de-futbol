package com.overcode.persistence.dto.jpa.cotizacion;

import com.overcode.model.cotizacion.EstrategiaCotizacion;
import com.overcode.model.cotizacion.EstrategiaCotizacionRendimientoTotal;
import com.overcode.persistence.dto.jpa.cotizacion.mapper.MapEstrategiaAJPADTO;


public class EstrategiaCotizacionRendimientoTotalMapAJPADTO extends MapEstrategiaAJPADTO {

    public EstrategiaCotizacionRendimientoTotalMapAJPADTO() {
        super(EstrategiaCotizacionRendimientoTotal.class);
    }

    @Override
    public EstrategiaCotizacionJPADTO getDTO(EstrategiaCotizacion estrategiaCotizacion) {
        return new EstrategiaCotizacionRendimientoTotalJPADTO();
    }
}
