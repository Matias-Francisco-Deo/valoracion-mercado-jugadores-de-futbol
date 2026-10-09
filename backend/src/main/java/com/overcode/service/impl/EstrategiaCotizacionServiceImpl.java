package com.overcode.service.impl;

import com.overcode.model.Player;
import com.overcode.model.cotizacion.ConfiguracionCotizaciones;
import com.overcode.model.cotizacion.EstrategiaCotizacion;
import com.overcode.model.exception.EstrategiaInvalidaException;
import com.overcode.persistence.repository.interfaces.EstrategiaCotizacionRepository;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.interfaces.ConfiguracionCotizacionesService;
import com.overcode.service.interfaces.EstrategiaCotizacionService;
import com.overcode.service.interfaces.PlayerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EstrategiaCotizacionServiceImpl implements EstrategiaCotizacionService {

    private final EstrategiaCotizacionRepository estrategiaCotizacionRepository;
    private final ConfiguracionCotizacionesService configuracionCotizacionesService;
    private final PlayerService playerService;

    public EstrategiaCotizacionServiceImpl(EstrategiaCotizacionRepository estrategiaCotizacionRepository, ConfiguracionCotizacionesService configuracionCotizacionesService, PlayerService playerService) {
        this.estrategiaCotizacionRepository = estrategiaCotizacionRepository;
        this.configuracionCotizacionesService = configuracionCotizacionesService;
        this.playerService = playerService;
    }


    @Override
    public EstrategiaCotizacion guardar(EstrategiaCotizacion estrategiaCotizacion) {
        return estrategiaCotizacionRepository.guardar(estrategiaCotizacion);
    }

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

    @Override
    public void actualizar(Long id, Double factorEscala) {

        verificarFactorEscalaPositivo(factorEscala);

        estrategiaCotizacionRepository.actualizar(id, factorEscala);
    }

    @Override
    public void cotizarJugadores() {
        ConfiguracionCotizaciones configuracionCotizaciones = configuracionCotizacionesService.recuperar();

        EstrategiaCotizacion estrategia = configuracionCotizaciones.getEstrategiaCotizacion();

        List<Player> jugadores = playerService.recuperarTodos();

        estrategia.cotizar(jugadores);

        playerService.guardarTodos(jugadores);

    }

    private static void verificarFactorEscalaPositivo(Double factorEscala) {
        if (factorEscala == null || factorEscala <= 0) throw new EstrategiaInvalidaException("El factor escala debe ser mayor a 0");
    }

}
