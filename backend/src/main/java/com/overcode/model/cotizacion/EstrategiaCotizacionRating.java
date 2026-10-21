package com.overcode.model.cotizacion;

import com.overcode.model.player.Player;

public class EstrategiaCotizacionRating extends EstrategiaCotizacion {

    public EstrategiaCotizacionRating() {
        super("Cotización basada en rating exclusivamente", 100.0, 1.8);
    }

    @Override
    public Double calcularScore(Player player) {
        return player.getPlayerGameData().getRating() * 50;
    }
}
