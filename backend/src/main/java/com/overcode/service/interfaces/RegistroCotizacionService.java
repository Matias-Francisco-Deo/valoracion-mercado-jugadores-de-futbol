package com.overcode.service.interfaces;

import com.overcode.model.cotizacion.RegistroCotizacion;

import java.time.LocalDateTime;
import java.util.List;

public interface RegistroCotizacionService {
    List<RegistroCotizacion> recuperarRegistrosDeJugador(Long playerId, LocalDateTime inicio, LocalDateTime fin);
}
