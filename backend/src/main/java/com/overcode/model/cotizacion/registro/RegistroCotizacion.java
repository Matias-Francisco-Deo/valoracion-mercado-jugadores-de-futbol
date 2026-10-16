package com.overcode.model.cotizacion.registro;

import com.overcode.model.Player;
import com.overcode.model.cotizacion.EstrategiaCotizacion;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class RegistroCotizacion {

    private Long id;

    private Player player;

    private LocalDateTime createdAt;

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
