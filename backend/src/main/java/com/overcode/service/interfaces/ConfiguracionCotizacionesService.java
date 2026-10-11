package com.overcode.service.interfaces;

import com.overcode.model.cotizacion.ConfiguracionCotizaciones;

public interface ConfiguracionCotizacionesService {
    ConfiguracionCotizaciones crearConfiguracionSiNoExiste(ConfiguracionCotizaciones configuracionCotizaciones);
    ConfiguracionCotizaciones recuperar();
}
