package com.overcode.persistence.repository.interfaces;

import com.overcode.model.cotizacion.ConfiguracionCotizaciones;

public interface ConfiguracionCotizacionesRepository {
    ConfiguracionCotizaciones guardar(ConfiguracionCotizaciones configuracionCotizaciones);
    ConfiguracionCotizaciones recuperar();

}
