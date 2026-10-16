package com.overcode.persistence.repository.impl;

import com.overcode.model.cotizacion.registro.BusquedaRegistroCotizacion;
import com.overcode.model.cotizacion.registro.RegistroCotizacion;
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
    public List<RegistroCotizacion> guardarTodos(List<RegistroCotizacion> registroCotizaciones) {

        List<RegistroCotizacionJPADTO> registros =
                registroCotizaciones.stream().map(RegistroCotizacionJPADTO::desdeModelo).toList();

        return registroCotizacionDAOJPA.saveAll(registros).stream().map(RegistroCotizacionJPADTO::aModelo).toList();
    }

    @Override
    public List<RegistroCotizacion> recuperarRegistrosDeJugador(BusquedaRegistroCotizacion busquedaRegistroCotizacion) {
        return registroCotizacionDAOJPA.recuperarRegistrosDeJugador(
                        busquedaRegistroCotizacion.playerId(),
                        busquedaRegistroCotizacion.inicio(),
                        busquedaRegistroCotizacion.fin()
                ).stream()
                .map(RegistroCotizacionJPADTO::aModelo).toList();
    }


}
