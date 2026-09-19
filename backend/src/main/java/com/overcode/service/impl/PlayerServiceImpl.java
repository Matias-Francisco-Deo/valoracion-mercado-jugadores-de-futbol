package com.overcode.service.impl;

import com.overcode.model.Player;
import com.overcode.model.WeeklyMetrics;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.exception.NombreRepetidoException;
import com.overcode.service.interfaces.PlayerService;
import com.overcode.service.interfaces.PlayerMetricsProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerMetricsProvider metricsProvider;

    public PlayerServiceImpl(PlayerRepository playerRepository, PlayerMetricsProvider metricsProvider) {
        this.playerRepository = playerRepository;
        this.metricsProvider = metricsProvider;
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
    @Transactional
    public void sincronizarMetricas() {
        List<Player> players = playerRepository.listarTodos();
        for (Player p : players) {
            WeeklyMetrics metrics = metricsProvider.getPlayerMetrics(p.getClubName(), p.getName());
            if (metrics != null) {
                p.actualizarMetricas(metrics);
                playerRepository.guardar(p);
                System.out.println("✅ Métricas actualizadas para: " + p.getName());
            }
        }
    }

    private void validarJugador(Player player) {
        if (playerRepository.existsByName(player.getName())) {
            throw new NombreRepetidoException("El nombre del jugador ya existe: " + player.getName());
        }

    }
}
