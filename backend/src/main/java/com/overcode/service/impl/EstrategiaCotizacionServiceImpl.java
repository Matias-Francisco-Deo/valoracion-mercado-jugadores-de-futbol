package com.overcode.service.impl;

import com.overcode.model.cotizacion.ConfiguracionCotizaciones;
import com.overcode.model.cotizacion.EstrategiaCotizacion;
import com.overcode.persistence.repository.interfaces.EstrategiaCotizacionRepository;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.interfaces.ConfiguracionCotizacionesService;
import com.overcode.service.interfaces.EstrategiaCotizacionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EstrategiaCotizacionServiceImpl implements EstrategiaCotizacionService {

    private final EstrategiaCotizacionRepository estrategiaCotizacionRepository;
    private final ConfiguracionCotizacionesService configuracionCotizacionesService;

    public EstrategiaCotizacionServiceImpl(EstrategiaCotizacionRepository estrategiaCotizacionRepository, ConfiguracionCotizacionesService configuracionCotizacionesService) {
        this.estrategiaCotizacionRepository = estrategiaCotizacionRepository;
        this.configuracionCotizacionesService = configuracionCotizacionesService;
    }


    @Override
    public EstrategiaCotizacion guardar(EstrategiaCotizacion estrategiaCotizacion) {
        return estrategiaCotizacionRepository.guardar(estrategiaCotizacion);
    }

    // TODO hacer update


    @Override
    public List<EstrategiaCotizacion> recuperarTodos() {
        return estrategiaCotizacionRepository.recuperarTodos();
    }

    @Override
    public void seleccionarEstrategia(Long id) {
        Optional<EstrategiaCotizacion> optionalEstrategiaCotizacion = estrategiaCotizacionRepository.recuperar(id);
        ConfiguracionCotizaciones configuracionCotizaciones = configuracionCotizacionesService.recuperar();

        if (optionalEstrategiaCotizacion.isEmpty()) throw new EntidadNoEncontradaException("La estrategia a seleccionar no existe");

        EstrategiaCotizacion estrategiaCotizacion = optionalEstrategiaCotizacion.get();

        configuracionCotizaciones.cambiarEstrategiaDeCotizacionPor(estrategiaCotizacion);

        configuracionCotizacionesService.guardar(configuracionCotizaciones);


    }

}
