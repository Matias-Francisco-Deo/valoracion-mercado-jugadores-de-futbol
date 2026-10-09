package com.overcode.persistence.repository.impl;

import com.overcode.controller.dto.player.PlayerFilter;
import com.overcode.model.Player;
import com.overcode.persistence.dto.jpa.PlayerJPADTO;
import com.overcode.persistence.repository.dao.jpa.PlayerDAOJPA;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PlayerRepositoryImpl implements PlayerRepository {

    private final PlayerDAOJPA playerDAOJPA;

    public PlayerRepositoryImpl(PlayerDAOJPA playerDAOJPA) {
        this.playerDAOJPA = playerDAOJPA;
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
    public Page<Player> listarJugadores(PlayerFilter filtro, Pageable pageable) {
        return playerDAOJPA.listarJugadores(filtro.getClubName(), filtro.getLeague(),pageable).map(PlayerJPADTO::aModelo);
    }

    @Override//temporal hasta preguntar si necesitamos traer todos sin paginar
    public List<Player> listarTodos() {
        return playerDAOJPA.findAll().stream().map(PlayerJPADTO::aModelo).toList();
    }

    @Override
    public List<Player> guardarTodos(List<Player> jugadores) {
        List<PlayerJPADTO> playerJPADTOS = playerDAOJPA.saveAll(jugadores.stream().map(PlayerJPADTO::desdeModelo).toList());
        return playerJPADTOS.stream().map(PlayerJPADTO::aModelo).toList();
    }


    @Override
    public List<Player> listarTop5JugadoresPorRating() {
        return playerDAOJPA.listarTop5JugadoresPorRating().stream()
            .map(PlayerJPADTO::aModelo)
            .toList();
    }
}
