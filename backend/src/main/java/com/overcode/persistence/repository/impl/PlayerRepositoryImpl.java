package com.overcode.persistence.repository.impl;

import com.overcode.controller.dto.player.PlayerFilter;
import com.overcode.model.Player;
import com.overcode.persistence.dto.jpa.PlayerJPADTO;
import com.overcode.persistence.repository.dao.jpa.PlayerDAOJPA;
import com.overcode.persistence.repository.interfaces.ExternalPlayerRepository;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PlayerRepositoryImpl implements PlayerRepository {

    private final PlayerDAOJPA playerDAOJPA;
    private final ExternalPlayerRepository externalPlayerRepository;

    public PlayerRepositoryImpl(PlayerDAOJPA playerDAOJPA, ExternalPlayerRepository externalPlayerRepository) {
        this.playerDAOJPA = playerDAOJPA;
        this.externalPlayerRepository = externalPlayerRepository;
    }

    @Override
    public boolean existsByName(String name) {
        return playerDAOJPA.existsByNameIgnoreCase(name);
    }

    @Override
    public Player guardar(Player player) {
        PlayerJPADTO dto = PlayerJPADTO.desdeModelo(player);
        PlayerJPADTO playerDto = playerDAOJPA.save(dto);
        return playerDto.aModelo();
    }

    @Override
    public Optional<Player> recuperar(Long id) {
        return playerDAOJPA.findById(id).map(PlayerJPADTO::aModelo);
    }

    @Override
    public List<Player> recuperarTodosConFiltro(PlayerFilter filter) {
        return filter.getFilteredPlayers(playerDAOJPA);
    }


    @Override
    public List<Player> actualizarDatosJugadores() {
        return externalPlayerRepository.buscarYGuardarJugadores().orElse(List.of());
    }

    @Override
    public List<Player> listarTop5JugadoresPorRating() {
        return playerDAOJPA.listarTop5JugadoresPorRating().stream()
            .map(PlayerJPADTO::aModelo)
            .toList();
    }
}
