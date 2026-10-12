package com.overcode.testUtils.cotizacion;

import com.overcode.model.Player;
import com.overcode.model.cotizacion.EstrategiaCotizacion;

public class EstrategiaCotizacionSiempre2 extends EstrategiaCotizacion {

    public EstrategiaCotizacionSiempre2(Double valorBase, Double factorEscala) {
        setTitulo("Estrategia Siempre 2");
        setValorBase(valorBase);
        setFactorEscala(factorEscala);
    }

    @Override
    public Double calcularScore(Player player) {
        return 2D;
    }
}
