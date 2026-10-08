package com.overcode.service.interfaces;

import com.overcode.model.cotizacion.ConfiguracionCotizaciones;

public interface ConfiguracionCotizacionesService {
    ConfiguracionCotizaciones guardar(ConfiguracionCotizaciones configuracionCotizaciones);
    ConfiguracionCotizaciones recuperar();
}
