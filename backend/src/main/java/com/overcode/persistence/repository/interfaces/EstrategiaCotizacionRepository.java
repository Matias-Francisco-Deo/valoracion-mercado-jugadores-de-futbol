package com.overcode.persistence.repository.interfaces;

import com.overcode.model.cotizacion.EstrategiaCotizacion;

import java.util.List;
import java.util.Optional;

public interface EstrategiaCotizacionRepository {
    EstrategiaCotizacion guardar(EstrategiaCotizacion estrategiaCotizacion);
    Optional<EstrategiaCotizacion> recuperar(Long id);
    List<EstrategiaCotizacion> recuperarTodos();
}
