package com.overcode.service.impl;

import com.overcode.model.cotizacion.ConfiguracionCotizaciones;
import com.overcode.persistence.repository.interfaces.ConfiguracionCotizacionesRepository;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.interfaces.ConfiguracionCotizacionesService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ConfiguracionCotizacionesServiceImpl implements ConfiguracionCotizacionesService {

    public static final long CONFIG_ID = 1L;

    private final ConfiguracionCotizacionesRepository configuracionCotizacionesRepository;

    public ConfiguracionCotizacionesServiceImpl(ConfiguracionCotizacionesRepository configuracionCotizacionesRepository) {
        this.configuracionCotizacionesRepository = configuracionCotizacionesRepository;
    }

    @Override
    public ConfiguracionCotizaciones crearConfiguracionSiNoExiste(ConfiguracionCotizaciones configuracionCotizaciones) {
        if (configuracionCotizacionesRepository.existePorId(CONFIG_ID)) return this.recuperar();
        configuracionCotizaciones.setId(CONFIG_ID); // siempre va a ser el mismo objeto porque tiene el mismo ID
        return configuracionCotizacionesRepository.guardar(configuracionCotizaciones);
    }


    @Override
    public ConfiguracionCotizaciones recuperar() {

        Optional<ConfiguracionCotizaciones> optionalConfig = configuracionCotizacionesRepository.recuperarPorId(CONFIG_ID);

        if (optionalConfig.isEmpty()) throw new EntidadNoEncontradaException("Configuración de cotizaciones no encontrada");

        return optionalConfig.get();
    }
}
