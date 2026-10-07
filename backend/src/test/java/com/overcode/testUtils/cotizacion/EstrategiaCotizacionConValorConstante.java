package com.overcode.testUtils.cotizacion;

import com.overcode.model.Player;
import com.overcode.model.cotizacion.EstrategiaCotizacion;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EstrategiaCotizacionConValorConstante extends EstrategiaCotizacion {

    private Double valorConstante;

    public EstrategiaCotizacionConValorConstante(Double valorConstante, Double factorEscala) {
        super(0D, factorEscala);
        setValorConstante(valorConstante);
    }

    @Override
    public Double calcularScore(Player player) {
        return getValorConstante();
    }
}
