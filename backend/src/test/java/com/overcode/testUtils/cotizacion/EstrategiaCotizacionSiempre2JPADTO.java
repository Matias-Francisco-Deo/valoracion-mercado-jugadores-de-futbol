package com.overcode.testUtils.cotizacion;

import com.overcode.model.cotizacion.EstrategiaCotizacion;
import com.overcode.persistence.dto.jpa.cotizacion.EstrategiaCotizacionJPADTO;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name="estrategia_cotizacion_siempre_2")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Setter
@Getter
@NoArgsConstructor
public class EstrategiaCotizacionSiempre2JPADTO extends EstrategiaCotizacionJPADTO {

    @Override
    public EstrategiaCotizacion aModelo() {
        EstrategiaCotizacion estrategiaCotizacion = new EstrategiaCotizacionSiempre2(getValorBase(), getFactorEscala());
        estrategiaCotizacion.setId(getId());
        return estrategiaCotizacion;
    }

    public static EstrategiaCotizacionSiempre2JPADTO desdeModeloParaTest(EstrategiaCotizacionSiempre2 estrategiaCotizacionSiempre2) {
        EstrategiaCotizacionSiempre2JPADTO dto = new EstrategiaCotizacionSiempre2JPADTO();
        dto.setTitulo(estrategiaCotizacionSiempre2.getTitulo());
        dto.setFactorEscala(estrategiaCotizacionSiempre2.getFactorEscala());
        dto.setValorBase(estrategiaCotizacionSiempre2.getValorBase());
        return dto;
    }
}
