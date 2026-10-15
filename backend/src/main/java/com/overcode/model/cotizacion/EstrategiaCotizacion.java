package com.overcode.model.cotizacion;

import com.overcode.model.Player;
import com.overcode.model.exception.EstrategiaInvalidaException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
public abstract class EstrategiaCotizacion {

    private Long id;
    private String titulo;
    private Double valorBase;
    private Double factorEscala;

    protected EstrategiaCotizacion(String titulo, Double valorBase, Double factorEscala) {
        setTitulo(titulo);
        setValorBase(valorBase);
        setFactorEscala(factorEscala);
    }

    public List<RegistroCotizacion> cotizar(List<Player> players) {
        return players.stream().map(this::cotizarAUnJugador).collect(Collectors.toCollection(ArrayList::new));
    }

    public RegistroCotizacion cotizarAUnJugador(Player player) {
        Double cotizacion = calcularCotizacion(player);
        player.recalcularCotizacion(cotizacion);
        return new RegistroCotizacion(player, cotizacion, this);
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

    public void setTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) throw new EstrategiaInvalidaException("El título es obligatorio");
        this.titulo = titulo;
    }
}
