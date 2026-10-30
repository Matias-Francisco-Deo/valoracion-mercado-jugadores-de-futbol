package com.overcode.service.interfaces;

import com.overcode.model.cotizacion.ConfiguracionMercado;

public interface ConfiguracionMercadoService {
    ConfiguracionMercado crearConfiguracionSiNoExiste(ConfiguracionMercado configuracionMercado);
    ConfiguracionMercado recuperar();
}
