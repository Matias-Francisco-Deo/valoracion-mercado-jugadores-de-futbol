package com.overcode.model.cotizacion;

import com.overcode.model.bonus.LoginBonus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ConfiguracionCotizaciones {
    private Long id;
    private EstrategiaCotizacion estrategiaCotizacion;
    private LoginBonus loginBonus;

    public ConfiguracionCotizaciones(EstrategiaCotizacion estrategiaCotizacion) {
        setEstrategiaCotizacion(estrategiaCotizacion);
    }

    public ConfiguracionCotizaciones(Long id, EstrategiaCotizacion estrategiaCotizacion) {
        setId(id);
        setEstrategiaCotizacion(estrategiaCotizacion);
    }

    public ConfiguracionCotizaciones(Long id, EstrategiaCotizacion estrategiaCotizacion, LoginBonus loginBonus) {
        setId(id);
        setEstrategiaCotizacion(estrategiaCotizacion);
        setLoginBonus(loginBonus);
    }

    public void cambiarEstrategiaDeCotizacionPor(EstrategiaCotizacion estrategiaCotizacion) {
        setEstrategiaCotizacion(estrategiaCotizacion);
    }

}
