package com.overcode.service.impl;

import com.overcode.model.cotizacion.RegistroCotizacion;
import com.overcode.persistence.repository.interfaces.RegistroCotizacionRepository;
import com.overcode.service.interfaces.RegistroCotizacionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class RegistroCotizacionServiceImpl implements RegistroCotizacionService {

    private final RegistroCotizacionRepository registroCotizacionRepository;

    public RegistroCotizacionServiceImpl(RegistroCotizacionRepository registroCotizacionRepository) {
        this.registroCotizacionRepository = registroCotizacionRepository;
    }

    @Override
    public List<RegistroCotizacion> recuperarRegistrosDeJugador(Long playerId, LocalDateTime inicio, LocalDateTime fin) {
        return registroCotizacionRepository.recuperarRegistrosDeJugador(playerId, inicio, fin);
    }
}
