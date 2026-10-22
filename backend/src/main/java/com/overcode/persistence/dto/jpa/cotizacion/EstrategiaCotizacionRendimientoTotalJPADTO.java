package com.overcode.persistence.dto.jpa.cotizacion;

import com.overcode.model.cotizacion.EstrategiaCotizacion;
import com.overcode.model.cotizacion.EstrategiaCotizacionRendimientoTotal;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name="estrategia_cotizacion_total")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Setter
@Getter
@NoArgsConstructor
public class EstrategiaCotizacionRendimientoTotalJPADTO extends EstrategiaCotizacionJPADTO {

    @Override
    public EstrategiaCotizacion aModelo() {
        EstrategiaCotizacion estrategiaCotizacion = new EstrategiaCotizacionRendimientoTotal();
        estrategiaCotizacion.setId(getId());
        return estrategiaCotizacion;
    }
}
