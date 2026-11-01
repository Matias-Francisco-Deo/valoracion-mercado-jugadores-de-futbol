package com.overcode.testUtils.cotizacion.rating;

import com.overcode.model.cotizacion.EstrategiaCotizacion;
import com.overcode.model.player.Player;

public class EstrategiaCotizacionTestRating extends EstrategiaCotizacion {

    public EstrategiaCotizacionTestRating(Double valorBase, Double factorEscala) {
        setTitulo("Estrategia Rating");
        setValorBase(valorBase);
        setFactorEscala(factorEscala);
    }

    @Override
    public Double calcularScore(Player player) {
        return player.getPlayerGameData().getRating();
    }
}
