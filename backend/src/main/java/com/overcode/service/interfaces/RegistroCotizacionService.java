package com.overcode.service.interfaces;

import com.overcode.model.cotizacion.registro.BusquedaRegistroCotizacion;
import com.overcode.model.cotizacion.registro.RegistroCotizacion;

import java.util.List;

public interface RegistroCotizacionService {
    List<RegistroCotizacion> recuperarRegistrosDeJugador(BusquedaRegistroCotizacion busquedaRegistroCotizacion);
}
