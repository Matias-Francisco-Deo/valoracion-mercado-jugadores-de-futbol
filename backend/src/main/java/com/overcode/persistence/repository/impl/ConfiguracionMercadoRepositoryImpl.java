package com.overcode.persistence.repository.impl;

import com.overcode.model.cotizacion.ConfiguracionMercado;
import com.overcode.persistence.dto.jpa.cotizacion.ConfiguracionMercadoJPADTO;
import com.overcode.persistence.repository.dao.jpa.ConfiguracionMercadoDAOJPA;
import com.overcode.persistence.repository.interfaces.ConfiguracionMercadoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ConfiguracionMercadoRepositoryImpl implements ConfiguracionMercadoRepository {
    private final ConfiguracionMercadoDAOJPA configuracionMercadoDAOJPA;

    public ConfiguracionMercadoRepositoryImpl(ConfiguracionMercadoDAOJPA configuracionMercadoDAOJPA) {
        this.configuracionMercadoDAOJPA = configuracionMercadoDAOJPA;
    }

    @Override
    public ConfiguracionMercado guardar(ConfiguracionMercado configuracionMercado) {
        ConfiguracionMercadoJPADTO dto = ConfiguracionMercadoJPADTO.desdeModelo(configuracionMercado);
        return configuracionMercadoDAOJPA.save(dto).aModelo();
    }

    @Override
    public Optional<ConfiguracionMercado> recuperarPorId(Long id) {
        Optional<ConfiguracionMercadoJPADTO> optionalConfig = configuracionMercadoDAOJPA.findById(id);

        return optionalConfig.map(ConfiguracionMercadoJPADTO::aModelo);

    }

    @Override
    public boolean existePorId(Long id) {
        return configuracionMercadoDAOJPA.existsById(id);
    }
}
