package com.overcode.persistence.repository.interfaces;

import com.overcode.model.cotizacion.RegistroCotizacion;

import java.util.List;

public interface RegistroCotizacionRepository {
    List<RegistroCotizacion> guardarTodos(List<RegistroCotizacion> registroCotizaciones);
    List<RegistroCotizacion> recuperarRegistrosDeJugador(Long playerId);
}
