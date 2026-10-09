package com.overcode.persistence.repository.interfaces;

import com.overcode.model.cotizacion.ConfiguracionCotizaciones;

import java.util.Optional;

public interface ConfiguracionCotizacionesRepository {
    ConfiguracionCotizaciones guardar(ConfiguracionCotizaciones configuracionCotizaciones);
    Optional<ConfiguracionCotizaciones> recuperar();

}
