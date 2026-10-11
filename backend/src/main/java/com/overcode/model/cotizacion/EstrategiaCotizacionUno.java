package com.overcode.model.cotizacion;

import com.overcode.model.Player;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EstrategiaCotizacionUno extends EstrategiaCotizacion {

    private Double valorConstante;

    public EstrategiaCotizacionUno(Double valorConstante, Double factorEscala) {
        super(0D, factorEscala);
        setValorConstante(valorConstante);
    }

    public EstrategiaCotizacionUno(Double valorBase) {
        super(valorBase, 1.0);
        setValorConstante(0.0);
    }

    @Override
    public Double calcularScore(Player player) {
        return getValorConstante();
    }
}
