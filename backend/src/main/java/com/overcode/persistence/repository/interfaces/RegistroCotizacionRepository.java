package com.overcode.persistence.repository.interfaces;

import com.overcode.model.cotizacion.registro.BusquedaRegistroCotizacion;
import com.overcode.model.cotizacion.registro.RegistroCotizacion;

import java.util.List;

public interface RegistroCotizacionRepository {
    List<RegistroCotizacion> guardarTodos(List<RegistroCotizacion> registroCotizaciones);
    List<RegistroCotizacion> recuperarRegistrosDeJugador(BusquedaRegistroCotizacion busquedaRegistroCotizacion);
}
