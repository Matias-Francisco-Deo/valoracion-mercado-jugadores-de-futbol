package com.overcode.testUtils.cotizacion.rating;

import com.overcode.model.cotizacion.EstrategiaCotizacion;
import com.overcode.persistence.dto.jpa.cotizacion.EstrategiaCotizacionJPADTO;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name="estrategia_rating_test")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Setter
@Getter
@NoArgsConstructor
public class EstrategiaCotizacionTestRatingJPADTO extends EstrategiaCotizacionJPADTO {

    @Override
    public EstrategiaCotizacion aModelo() {
        EstrategiaCotizacion estrategiaCotizacion = new EstrategiaCotizacionTestRating(getValorBase(), getFactorEscala());
        estrategiaCotizacion.setId(getId());
        return estrategiaCotizacion;
    }
}
