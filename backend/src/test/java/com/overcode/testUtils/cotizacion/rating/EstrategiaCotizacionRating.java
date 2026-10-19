package com.overcode.testUtils.cotizacion.rating;

import com.overcode.model.Player;
import com.overcode.model.cotizacion.EstrategiaCotizacion;

public class EstrategiaCotizacionRating extends EstrategiaCotizacion {

    public EstrategiaCotizacionRating(Double valorBase, Double factorEscala) {
        setTitulo("Estrategia Rating");
        setValorBase(valorBase);
        setFactorEscala(factorEscala);
    }

    @Override
    public Double calcularScore(Player player) {
        return player.getPlayerGameData().getRating();
    }
}
