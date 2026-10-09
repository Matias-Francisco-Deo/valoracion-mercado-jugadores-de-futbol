package com.overcode.service.impl;

import com.overcode.controller.dto.player.PlayerFilter;
import com.overcode.model.Player;
import com.overcode.model.Team;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import com.overcode.persistence.repository.interfaces.TeamRepository;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.exception.NombreRepetidoException;
import com.overcode.service.interfaces.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private static final Logger log = LoggerFactory.getLogger(PlayerServiceImpl.class);

    public PlayerServiceImpl(PlayerRepository playerRepository, TeamRepository teamRepository) {
        this.playerRepository = playerRepository;
        this.teamRepository=teamRepository;
    }

    @Override
    public Player crear(Player player) {
        validarJugador(player);

        Team team = player.getTeam();

        Optional<Team> teamOptional = teamRepository.recuperarPorNombreYLiga(team.getName(), team.getLeague());
        Team teamGuardado = teamOptional.orElseGet(() -> teamRepository.guardar(team));

        player.setTeam(teamGuardado);
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
    public Page<Player> recuperarTodosConFiltro(PlayerFilter filter, Pageable pageable) {
        return playerRepository.listarJugadores(filter, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Player> recuperarTodos() {
        return playerRepository.listarTodos();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Player> listarTop5JugadoresPorRating() {
        return playerRepository.listarTop5JugadoresPorRating();
    }

    @Override
    public void guardarTodos(List<Player> jugadores) {
        playerRepository.guardarTodos(jugadores);
    }

    private void validarJugador(Player player) {
        if (playerRepository.existsByName(player.getName())) {
            log.error("Jugador ya existe: {}", player.getName());
            throw new NombreRepetidoException("El nombre del jugador ya existe: " + player.getName());
        }

    }
}
