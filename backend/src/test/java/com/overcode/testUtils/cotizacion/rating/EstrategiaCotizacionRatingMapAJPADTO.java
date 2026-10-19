package com.overcode.testUtils.cotizacion.rating;

import com.overcode.model.cotizacion.EstrategiaCotizacion;
import com.overcode.persistence.dto.jpa.cotizacion.EstrategiaCotizacionJPADTO;
import com.overcode.persistence.dto.jpa.cotizacion.MapEstrategiaAJPADTO;


public class EstrategiaCotizacionRatingMapAJPADTO extends MapEstrategiaAJPADTO {

    public EstrategiaCotizacionRatingMapAJPADTO() {
        super(EstrategiaCotizacionRating.class);
    }

    @Override
    public EstrategiaCotizacionJPADTO getDTO(EstrategiaCotizacion estrategiaCotizacion) {
        return new EstrategiaCotizacionRatingJPADTO();
    }
}
