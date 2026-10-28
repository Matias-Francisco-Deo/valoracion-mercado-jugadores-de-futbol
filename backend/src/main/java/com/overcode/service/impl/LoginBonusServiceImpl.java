package com.overcode.service.impl;

import com.overcode.model.bonus.LoginBonus;
import com.overcode.model.cotizacion.ConfiguracionCotizaciones;
import com.overcode.service.interfaces.ConfiguracionCotizacionesService;
import com.overcode.service.interfaces.LoginBonusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LoginBonusServiceImpl implements LoginBonusService {

    private final ConfiguracionCotizacionesService configuracionCotizacionesService;

    public LoginBonusServiceImpl(ConfiguracionCotizacionesService configuracionCotizacionesService) {
        this.configuracionCotizacionesService = configuracionCotizacionesService;
    }

    @Override
    public LoginBonus recuperarSeleccionado() {
        ConfiguracionCotizaciones configuracion = configuracionCotizacionesService.recuperar();
        return configuracion.getLoginBonus(); // TODO cambiarle nombre a la config
    }
}
