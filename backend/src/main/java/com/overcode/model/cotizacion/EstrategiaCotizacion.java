package com.overcode.model.cotizacion;

import com.overcode.model.Player;
import com.overcode.model.exception.EstrategiaInvalidaException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public abstract class EstrategiaCotizacion {

    private Long id;
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
        return Math.max(getValorBase() + (score * getFactorEscala()), 1);
    }

    public abstract Double calcularScore(Player player);

    public void setFactorEscala(Double factorEscala) {
        if (factorEscala == null || factorEscala <= 0) throw new EstrategiaInvalidaException("El factor escala debe ser mayor a 0");
        this.factorEscala = factorEscala;
    }

    public void setValorBase(Double valorBase) {
        if (valorBase == null || valorBase < 0) throw new EstrategiaInvalidaException("El valor base debe ser 0 o más grande");
        this.valorBase = valorBase;
    }
}
