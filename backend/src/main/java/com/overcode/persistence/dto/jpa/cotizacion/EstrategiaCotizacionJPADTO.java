package com.overcode.persistence.dto.jpa.cotizacion;

import com.overcode.model.cotizacion.EstrategiaCotizacion;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name="estrategia_cotizacion")
@Table(name = "estrategias_cotizacion")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Setter
@Getter
@NoArgsConstructor
public abstract class EstrategiaCotizacionJPADTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "valor_base")
    @NotNull
    private Double valorBase;

    @Column(name = "factor_escala")
    @NotNull
    private Double factorEscala;

    public abstract EstrategiaCotizacion instanciarEstrategia(Double valorBase, Double factorEscala);

    public EstrategiaCotizacion aModelo() {
        EstrategiaCotizacion estrategiaCotizacion = instanciarEstrategia(getValorBase(), getFactorEscala());
        estrategiaCotizacion.setId(getId());
        return estrategiaCotizacion;
    }

    public static EstrategiaCotizacionJPADTO desdeModelo(EstrategiaCotizacion estrategia) {
        EstrategiaCotizacionJPADTO dto = TipoEstrategiaCotizacion.getValueOf(estrategia.getClass());
        dto.setId(estrategia.getId());
        dto.setValorBase(estrategia.getValorBase());
        dto.setFactorEscala(estrategia.getFactorEscala());
        return dto;
    }


}
