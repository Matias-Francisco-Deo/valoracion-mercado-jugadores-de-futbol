package com.overcode.model.cotizacion;

import com.overcode.model.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class RegistroCotizacion {

    private Long id;

    private Player player;

    private LocalDate createdAt;

    private Double cotizacion;

    private EstrategiaCotizacion estrategiaCotizacionUsada;

    public RegistroCotizacion(Player player, Double cotizacion, EstrategiaCotizacion estrategiaCotizacionUsada) {
        setPlayer(player);
        setCotizacion(cotizacion);
        setEstrategiaCotizacionUsada(estrategiaCotizacionUsada);
    }

    public void setCotizacion(Double cotizacion) {
        this.cotizacion = Math.max(1, cotizacion);
    }
}
