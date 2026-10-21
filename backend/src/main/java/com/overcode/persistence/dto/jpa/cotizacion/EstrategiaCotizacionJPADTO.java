package com.overcode.persistence.dto.jpa.cotizacion;

import com.overcode.model.cotizacion.EstrategiaCotizacion;
import com.overcode.persistence.dto.jpa.cotizacion.mapper.TipoEstrategiaCotizacion;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

    @Column(name = "titulo")
    @NotNull
    @NotBlank
    private String titulo;

    @Column(name = "valor_base")
    @NotNull
    private Double valorBase;

    @Column(name = "factor_escala")
    @NotNull
    private Double factorEscala;

    public abstract EstrategiaCotizacion aModelo();

    public static EstrategiaCotizacionJPADTO desdeModelo(EstrategiaCotizacion estrategia) {
        EstrategiaCotizacionJPADTO dto = TipoEstrategiaCotizacion.getValueOf(estrategia);
        dto.setId(estrategia.getId());
        dto.setTitulo(estrategia.getTitulo());
        dto.setValorBase(estrategia.getValorBase());
        dto.setFactorEscala(estrategia.getFactorEscala());
        return dto;
    }


}
