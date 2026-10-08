package com.overcode.model.cotizacion;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ConfiguracionCotizaciones {
    private Long id;
    private EstrategiaCotizacion estrategiaCotizacion;

    public ConfiguracionCotizaciones(EstrategiaCotizacion estrategiaCotizacion) {
        setEstrategiaCotizacion(estrategiaCotizacion);
    }

    public void cambiarEstrategiaDeCotizacionPor(EstrategiaCotizacion estrategiaCotizacion) {
        setEstrategiaCotizacion(estrategiaCotizacion);
    }
}
