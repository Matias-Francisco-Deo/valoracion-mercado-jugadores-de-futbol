package com.overcode.persistence.repository.impl;

import com.overcode.model.Player;
import com.overcode.persistence.dto.PlayerJPADTO;
import com.overcode.persistence.dto.UserJPADTO;
import com.overcode.persistence.repository.dao.PlayerDAOJPA;
import com.overcode.persistence.repository.dao.UserDAOJPA;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PlayerRepositoryImpl implements PlayerRepository {

    private final PlayerDAOJPA playerDAOJPA;

    public PlayerRepositoryImpl(PlayerDAOJPA playerDAOJPA) {
        this.playerDAOJPA = playerDAOJPA;
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
}
