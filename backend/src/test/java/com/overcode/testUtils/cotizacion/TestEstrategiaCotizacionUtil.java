package com.overcode.testUtils.cotizacion;

import com.overcode.model.cotizacion.EstrategiaCotizacion;

public class TestEstrategiaCotizacionUtil {
    public static EstrategiaCotizacion getEstrategiaCotizacionConFactorEscalaYValorConstante(Double factorEscala, Double valorConstante) {
        return new EstrategiaCotizacionConValorConstante(valorConstante, factorEscala);
    }

    public static EstrategiaCotizacion getEstrategiaCotizacionConFactorEscala(Double factorEscala) {
        return new EstrategiaCotizacionConValorConstante(1.0, factorEscala);
    }

    public static EstrategiaCotizacion getEstrategiaConValorBase(Double valorBase) {
        return new EstrategiaCotizacionConValorConstante(valorBase);
    }
}
