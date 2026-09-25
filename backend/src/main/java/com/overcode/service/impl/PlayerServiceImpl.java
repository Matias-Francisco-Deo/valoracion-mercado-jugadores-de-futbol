package com.overcode.service.impl;

import com.overcode.model.Player;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.exception.NombreRepetidoException;
import com.overcode.service.interfaces.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private static final Logger log = LoggerFactory.getLogger(PlayerServiceImpl.class);

    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    @Transactional
    public Player crear(Player player) {
        validarJugador(player);
        return playerRepository.guardar(player);
    }

    @Override
    @Transactional(readOnly = true)
    public Player recuperar(Long id) {
        return playerRepository.recuperar(id)
            .orElseThrow(() -> new EntidadNoEncontradaException("Jugador no encontrado."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Player> recuperarTodos() {
        return playerRepository.listarTodos();
    }

    @Override
    public List<Player> actualizarDatosJugadores() {
        return playerRepository.actualizarDatosJugadores();
    }

    private void validarJugador(Player player) {
        if (playerRepository.existsByName(player.getName())) {
            throw new NombreRepetidoException("El nombre del jugador ya existe: " + player.getName());
        }

    }
}
