package com.overcode.model.cotizacion;

import com.overcode.model.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public abstract class EstrategiaCotizacion {

    private Double valorBase;
    private Double factorEscala;

    protected EstrategiaCotizacion(Double valorBase, Double factorEscala) {
        setValorBase(valorBase);
        setFactorEscala(factorEscala);
    }

    public void cotizar(List<Player> players) {
        players.forEach(player -> {
            Double cotizacion = calcularCotizacion(player);
            player.recalcularCotizacion(cotizacion);
        });
    }

    public Double calcularCotizacion(Player player) {
        Double score = calcularScore(player);
        return getValorBase() + (score * getFactorEscala());
    }

    public abstract Double calcularScore(Player player);

}
