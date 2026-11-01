package com.overcode.service.impl;

import com.overcode.model.cotizacion.ConfiguracionMercado;
import com.overcode.persistence.repository.interfaces.ConfiguracionMercadoRepository;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.interfaces.ConfiguracionMercadoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ConfiguracionMercadoServiceImpl implements ConfiguracionMercadoService {

    public static final long CONFIG_ID = 1L;

    private final ConfiguracionMercadoRepository configuracionMercadoRepository;

    public ConfiguracionMercadoServiceImpl(ConfiguracionMercadoRepository configuracionMercadoRepository) {
        this.configuracionMercadoRepository = configuracionMercadoRepository;
    }

    @Override
    public ConfiguracionMercado crearConfiguracionSiNoExiste(ConfiguracionMercado configuracionMercado) {
        if (configuracionMercadoRepository.existePorId(CONFIG_ID)) return this.recuperar();
        configuracionMercado.setId(CONFIG_ID); // siempre va a ser el mismo objeto porque tiene el mismo ID
        return configuracionMercadoRepository.guardar(configuracionMercado);
    }


    @Override
    public ConfiguracionMercado recuperar() {

        Optional<ConfiguracionMercado> optionalConfig = configuracionMercadoRepository.recuperarPorId(CONFIG_ID);

        if (optionalConfig.isEmpty()) throw new EntidadNoEncontradaException("Configuración de cotizaciones no encontrada");

        return optionalConfig.get();
    }
}
