package com.overcode.service.interfaces;

import com.overcode.model.cotizacion.EstrategiaCotizacion;

import java.util.List;

public interface EstrategiaCotizacionService {

    EstrategiaCotizacion guardar(EstrategiaCotizacion estrategiaCotizacion);

    List<EstrategiaCotizacion> recuperarTodos();

    void seleccionarEstrategia(Long id);

    void actualizar(Long id, Double factorEscala);

    void cotizarJugadores();
}
