package com.overcode.model;

import com.overcode.model.cotizacion.EstrategiaCotizacion;
import com.overcode.model.cotizacion.registro.RegistroCotizacion;
import com.overcode.testUtils.cotizacion.EstrategiaCotizacionSiempre2;
import org.junit.jupiter.api.Test;

import static com.overcode.testUtils.TestPlayerUtil.getJugadorConNombre;
import static junit.framework.TestCase.assertEquals;

public class RegistroCotizacionTest {

    public static Player JUGADOR_1 = getJugadorConNombre("Rich");
    private final EstrategiaCotizacion estrategiaCotizacion = new EstrategiaCotizacionSiempre2(0.0, 1.0);

    @Test
    void elRegistroTieneComoCotizacionMinima1() {
        RegistroCotizacion registro = new RegistroCotizacion(JUGADOR_1, 0.0, estrategiaCotizacion);
        assertEquals(1.0, registro.getCotizacion());
    }
}
