package com.overcode.persistence.dto.jpa.cotizacion.mapper;

import com.overcode.model.cotizacion.EstrategiaCotizacion;
import com.overcode.model.cotizacion.EstrategiaCotizacionRating;
import com.overcode.persistence.dto.jpa.cotizacion.EstrategiaCotizacionJPADTO;
import com.overcode.persistence.dto.jpa.cotizacion.EstrategiaCotizacionRatingJPADTO;


public class EstrategiaCotizacionRatingMapAJPADTO extends MapEstrategiaAJPADTO {

    public EstrategiaCotizacionRatingMapAJPADTO() {
        super(EstrategiaCotizacionRating.class);
    }

    @Override
    public EstrategiaCotizacionJPADTO getDTO(EstrategiaCotizacion estrategiaCotizacion) {
        return new EstrategiaCotizacionRatingJPADTO();
    }
}
