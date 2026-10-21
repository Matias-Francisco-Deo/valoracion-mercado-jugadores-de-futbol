package com.overcode.model.cotizacion;

import com.overcode.model.player.Player;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EstrategiaCotizacionUno extends EstrategiaCotizacion {

    private Double valorConstante;

    public EstrategiaCotizacionUno(Double valorConstante, Double factorEscala) {
        super("Estrategia con valor constante " + valorConstante, 2D, factorEscala);
        setValorConstante(valorConstante);
    }

    @Override
    public Double calcularScore(Player player) {
        return getValorConstante();
    }
}
