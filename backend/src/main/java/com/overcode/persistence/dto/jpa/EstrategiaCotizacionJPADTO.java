package com.overcode.persistence.dto.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name="estrategia_cotizacion")
@Table(name = "estrategias_cotizacion")
@Setter
@Getter
@NoArgsConstructor
public class EstrategiaCotizacionJPADTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double valorBase;
    private Double factorEscala;

}
