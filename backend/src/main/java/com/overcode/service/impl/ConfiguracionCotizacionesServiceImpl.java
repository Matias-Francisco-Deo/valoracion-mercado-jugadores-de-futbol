package com.overcode.service.impl;

import com.overcode.model.cotizacion.ConfiguracionCotizaciones;
import com.overcode.persistence.repository.interfaces.ConfiguracionCotizacionesRepository;
import com.overcode.service.interfaces.ConfiguracionCotizacionesService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ConfiguracionCotizacionesServiceImpl implements ConfiguracionCotizacionesService {
    private final ConfiguracionCotizacionesRepository configuracionCotizacionesRepository;

    public ConfiguracionCotizacionesServiceImpl(ConfiguracionCotizacionesRepository configuracionCotizacionesRepository) {
        this.configuracionCotizacionesRepository = configuracionCotizacionesRepository;
    }

    @Override
    public ConfiguracionCotizaciones guardar(ConfiguracionCotizaciones configuracionCotizaciones) {
        return configuracionCotizacionesRepository.guardar(configuracionCotizaciones);
    }

    @Override
    public ConfiguracionCotizaciones recuperar() {
        return configuracionCotizacionesRepository.recuperar();
    }
}
