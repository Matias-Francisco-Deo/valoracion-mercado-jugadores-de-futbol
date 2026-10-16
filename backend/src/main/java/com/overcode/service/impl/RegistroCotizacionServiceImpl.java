package com.overcode.service.impl;

import com.overcode.model.cotizacion.registro.BusquedaRegistroCotizacion;
import com.overcode.model.cotizacion.registro.RegistroCotizacion;
import com.overcode.persistence.repository.interfaces.RegistroCotizacionRepository;
import com.overcode.service.interfaces.RegistroCotizacionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RegistroCotizacionServiceImpl implements RegistroCotizacionService {

    private final RegistroCotizacionRepository registroCotizacionRepository;

    public RegistroCotizacionServiceImpl(RegistroCotizacionRepository registroCotizacionRepository) {
        this.registroCotizacionRepository = registroCotizacionRepository;
    }

    @Override
    public List<RegistroCotizacion> recuperarRegistrosDeJugador(BusquedaRegistroCotizacion busquedaRegistroCotizacion) {
        return registroCotizacionRepository.recuperarRegistrosDeJugador(busquedaRegistroCotizacion);
    }
}
