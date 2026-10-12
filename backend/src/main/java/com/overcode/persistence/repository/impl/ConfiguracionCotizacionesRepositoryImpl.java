package com.overcode.persistence.repository.impl;

import com.overcode.model.cotizacion.ConfiguracionCotizaciones;
import com.overcode.persistence.dto.jpa.cotizacion.ConfiguracionCotizacionesJPADTO;
import com.overcode.persistence.repository.dao.jpa.ConfiguracionCotizacionesDAOJPA;
import com.overcode.persistence.repository.interfaces.ConfiguracionCotizacionesRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ConfiguracionCotizacionesRepositoryImpl implements ConfiguracionCotizacionesRepository {
    private final ConfiguracionCotizacionesDAOJPA configuracionCotizacionesDAOJPA;

    public ConfiguracionCotizacionesRepositoryImpl(ConfiguracionCotizacionesDAOJPA configuracionCotizacionesDAOJPA) {
        this.configuracionCotizacionesDAOJPA = configuracionCotizacionesDAOJPA;
    }

    @Override
    public ConfiguracionCotizaciones guardar(ConfiguracionCotizaciones configuracionCotizaciones) {
        ConfiguracionCotizacionesJPADTO dto = ConfiguracionCotizacionesJPADTO.desdeModelo(configuracionCotizaciones);
        return configuracionCotizacionesDAOJPA.save(dto).aModelo();
    }

    @Override
    public Optional<ConfiguracionCotizaciones> recuperarPorId(Long id) {
        Optional<ConfiguracionCotizacionesJPADTO> optionalConfig = configuracionCotizacionesDAOJPA.findById(id);

        return optionalConfig.map(ConfiguracionCotizacionesJPADTO::aModelo);

    }

    @Override
    public boolean existePorId(Long id) {
        return configuracionCotizacionesDAOJPA.existsById(id);
    }
}
