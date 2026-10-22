package com.overcode.model.cotizacion;

import com.overcode.model.bonus.LoginBonus;
import com.overcode.model.bonus.exception.LoginBonusInvalidoException;
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

    public ConfiguracionCotizaciones(Long idManual, EstrategiaCotizacion estrategiaCotizacion) {
        setId(idManual);
        setEstrategiaCotizacion(estrategiaCotizacion);
    }

    public ConfiguracionCotizaciones(Long idManual, EstrategiaCotizacion estrategiaCotizacion, LoginBonus loginBonus) {
        setId(idManual);
        setEstrategiaCotizacion(estrategiaCotizacion);
        setLoginBonus(loginBonus);
    }

    public void cambiarEstrategiaDeCotizacionPor(EstrategiaCotizacion estrategiaCotizacion) {
        setEstrategiaCotizacion(estrategiaCotizacion);
    }

    public void cambiarLoginBonusPor(LoginBonus nuevoBonus) {
        if (nuevoBonus == null) {
            throw new LoginBonusInvalidoException("El nuevo login bonus no puede ser nulo.");
        }
        this.loginBonus = nuevoBonus;
    }
}
