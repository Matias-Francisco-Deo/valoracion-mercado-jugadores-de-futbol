package com.overcode.testUtils.cotizacion.rating;

import com.overcode.model.cotizacion.EstrategiaCotizacion;
import com.overcode.persistence.dto.jpa.cotizacion.EstrategiaCotizacionJPADTO;
import com.overcode.persistence.dto.jpa.cotizacion.mapper.MapEstrategiaAJPADTO;


public class EstrategiaCotizacionTestRatingMapAJPADTO extends MapEstrategiaAJPADTO {

    public EstrategiaCotizacionTestRatingMapAJPADTO() {
        super(EstrategiaCotizacionTestRating.class);
    }

    @Override
    public EstrategiaCotizacionJPADTO getDTO(EstrategiaCotizacion estrategiaCotizacion) {
        return new EstrategiaCotizacionTestRatingJPADTO();
    }
}
