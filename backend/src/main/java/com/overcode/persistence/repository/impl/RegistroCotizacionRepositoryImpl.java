package com.overcode.persistence.repository.impl;

import com.overcode.model.cotizacion.RegistroCotizacion;
import com.overcode.persistence.dto.jpa.cotizacion.RegistroCotizacionJPADTO;
import com.overcode.persistence.repository.dao.jpa.RegistroCotizacionDAOJPA;
import com.overcode.persistence.repository.interfaces.RegistroCotizacionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RegistroCotizacionRepositoryImpl implements RegistroCotizacionRepository {

    private final RegistroCotizacionDAOJPA registroCotizacionDAOJPA;


    public RegistroCotizacionRepositoryImpl(RegistroCotizacionDAOJPA registroCotizacionDAOJPA) {
        this.registroCotizacionDAOJPA = registroCotizacionDAOJPA;
    }

    @Override
    public List<RegistroCotizacion> crearTodos(List<RegistroCotizacion> registroCotizaciones) {

        List<RegistroCotizacionJPADTO> registros =
                registroCotizaciones.stream().map(RegistroCotizacionJPADTO::desdeModelo).toList();

        return registroCotizacionDAOJPA.saveAll(registros).stream().map(RegistroCotizacionJPADTO::aModelo).toList();
    }

    public List<RegistroCotizacion> recuperarRegistrosDeJugador(Long playerId) {
        return registroCotizacionDAOJPA.recuperarRegistrosDeJugador(playerId).stream().map(RegistroCotizacionJPADTO::aModelo).toList();
    }
}
