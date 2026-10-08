package com.overcode.persistence.repository.impl;

import com.overcode.model.cotizacion.EstrategiaCotizacion;
import com.overcode.persistence.dto.jpa.cotizacion.EstrategiaCotizacionJPADTO;
import com.overcode.persistence.repository.dao.jpa.EstrategiaCotizacionDAOJPA;
import com.overcode.persistence.repository.interfaces.EstrategiaCotizacionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EstrategiaCotizacionRepositoryImpl implements EstrategiaCotizacionRepository {

    private final EstrategiaCotizacionDAOJPA estrategiaCotizacionDAOJPA;

    public EstrategiaCotizacionRepositoryImpl(EstrategiaCotizacionDAOJPA estrategiaCotizacionDAOJPA) {
        this.estrategiaCotizacionDAOJPA = estrategiaCotizacionDAOJPA;
    }

    @Override
    public EstrategiaCotizacion guardar(EstrategiaCotizacion estrategiaCotizacion) {
        return estrategiaCotizacionDAOJPA
                .save(EstrategiaCotizacionJPADTO.desdeModelo(estrategiaCotizacion))
                .aModelo();
    }

    @Override
    public Optional<EstrategiaCotizacion> recuperar(Long id) {
        return estrategiaCotizacionDAOJPA.findById(id)
                .map(EstrategiaCotizacionJPADTO::aModelo);
    }

    @Override
    public List<EstrategiaCotizacion> recuperarTodos() {
        return estrategiaCotizacionDAOJPA.findAll()
                .stream()
                .map(EstrategiaCotizacionJPADTO::aModelo)
                .toList();
    }
}
