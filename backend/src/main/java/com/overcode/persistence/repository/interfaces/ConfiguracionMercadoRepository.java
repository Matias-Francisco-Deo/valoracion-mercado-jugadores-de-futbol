package com.overcode.persistence.repository.interfaces;

import com.overcode.model.cotizacion.ConfiguracionMercado;

import java.util.Optional;

public interface ConfiguracionMercadoRepository {
    ConfiguracionMercado guardar(ConfiguracionMercado configuracionMercado);
    Optional<ConfiguracionMercado> recuperarPorId(Long id);
    boolean existePorId(Long id);
}
