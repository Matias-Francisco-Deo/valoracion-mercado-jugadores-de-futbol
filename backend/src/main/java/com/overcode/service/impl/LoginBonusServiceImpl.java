package com.overcode.service.impl;

import com.overcode.model.bonus.LoginBonus;
import com.overcode.model.cotizacion.ConfiguracionMercado;
import com.overcode.service.interfaces.ConfiguracionMercadoService;
import com.overcode.service.interfaces.LoginBonusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LoginBonusServiceImpl implements LoginBonusService {

    private final ConfiguracionMercadoService configuracionMercadoService;

    public LoginBonusServiceImpl(ConfiguracionMercadoService configuracionMercadoService) {
        this.configuracionMercadoService = configuracionMercadoService;
    }

    @Override
    public LoginBonus recuperarSeleccionado() {
        ConfiguracionMercado configuracion = configuracionMercadoService.recuperar();
        return configuracion.getLoginBonus();
    }
}
