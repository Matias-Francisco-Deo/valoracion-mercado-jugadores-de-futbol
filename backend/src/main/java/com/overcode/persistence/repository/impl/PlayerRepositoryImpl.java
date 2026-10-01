package com.overcode.persistence.repository.impl;

import com.overcode.controller.dto.player.PlayerFilter;
import com.overcode.model.Player;
import com.overcode.persistence.dto.jpa.PlayerJPADTO;
import com.overcode.persistence.repository.dao.jpa.PlayerDAOJPA;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
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
        PlayerJPADTO dto = PlayerJPADTO.desdeModelo(player, dto);
        PlayerJPADTO playerDto = playerDAOJPA.save(dto);
        return playerDto.aModelo(team);
    }

    @Override
    public Optional<Player> recuperar(Long id) {
        return playerDAOJPA.findById(id).map(playerJPADTO -> playerJPADTO.aModelo(team));
    }

    @Override
    public List<Player> listarJugadores(PlayerFilter filtro) {
        return playerDAOJPA.listarJugadores(filtro.getClubName(), filtro.getLeague()).stream().map(playerJPADTO -> playerJPADTO.aModelo(team)).toList();
    }


    @Override
    public List<Player> listarTop5JugadoresPorRating() {
        return playerDAOJPA.listarTop5JugadoresPorRating().stream()
            .map(playerJPADTO -> playerJPADTO.aModelo(team))
            .toList();
    }
}
