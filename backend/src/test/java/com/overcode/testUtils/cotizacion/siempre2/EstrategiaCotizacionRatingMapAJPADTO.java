package com.overcode.testUtils.cotizacion.siempre2;

import com.overcode.model.cotizacion.EstrategiaCotizacion;
import com.overcode.persistence.dto.jpa.cotizacion.EstrategiaCotizacionJPADTO;
import com.overcode.persistence.dto.jpa.cotizacion.MapEstrategiaAJPADTO;
import com.overcode.testUtils.cotizacion.rating.EstrategiaCotizacionRating;
import com.overcode.testUtils.cotizacion.rating.EstrategiaCotizacionRatingJPADTO;


public class EstrategiaCotizacionRatingMapAJPADTO extends MapEstrategiaAJPADTO {

    public EstrategiaCotizacionRatingMapAJPADTO() {
        super(EstrategiaCotizacionRating.class);
    }

    @Override
    public EstrategiaCotizacionJPADTO getDTO(EstrategiaCotizacion estrategiaCotizacion) {
        return new EstrategiaCotizacionRatingJPADTO();
    }
}
