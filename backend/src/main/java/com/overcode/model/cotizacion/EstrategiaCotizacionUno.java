package com.overcode.model.cotizacion;

import com.overcode.model.Player;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EstrategiaCotizacionUno extends EstrategiaCotizacion {

    private Double valorConstante;

    public EstrategiaCotizacionUno(Double valorConstante, Double factorEscala) {
        super("Estrategia con valor constante " + valorConstante, 0D, factorEscala);
        setValorConstante(valorConstante);
    }

    @Override
    public Double calcularScore(Player player) {
        return getValorConstante();
    }
}
