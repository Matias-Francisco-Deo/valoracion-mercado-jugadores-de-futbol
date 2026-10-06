package com.overcode.persistence.repository.impl;

import com.overcode.model.Player;
import com.overcode.model.PlayerGameData;
import com.overcode.persistence.dto.jpa.PlayerGameDataJPADTO;
import com.overcode.persistence.dto.jpa.PlayerJPADTO;
import com.overcode.persistence.repository.dao.jpa.PlayerGameDataDAOJPA;
import com.overcode.persistence.repository.interfaces.PlayerGameDataRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PlayerGameDataRepositoryImpl implements PlayerGameDataRepository {

    private final PlayerGameDataDAOJPA playerGameDataDAOJPA;

    public PlayerGameDataRepositoryImpl(PlayerGameDataDAOJPA playerGameDataDAOJPA) {
        this.playerGameDataDAOJPA = playerGameDataDAOJPA;
    }

    @Override
    public PlayerGameData guardar(PlayerGameData playerGameData) {
        PlayerGameDataJPADTO dto = PlayerGameDataJPADTO.desdeModelo(playerGameData);
        playerGameDataDAOJPA.save(dto);
        return playerGameData;
    }

    @Override
    public Optional<PlayerGameData> recuperar(Long id) {
        return playerGameDataDAOJPA.findById(id)
                .map(dto -> {
                    Player player = dto.getPlayer() != null ? mapPlayer(dto.getPlayer()) : null;
                    return dto.aModelo(player);
                });
    }

    private Player mapPlayer(PlayerJPADTO playerJPADTO) {
        if (playerJPADTO == null) {
            return null;
        }

        Player player = new Player();
        player.setId(playerJPADTO.getId());
        player.setExternalId(playerJPADTO.getExternalId());
        player.setName(playerJPADTO.getName());
        player.setCurrentPrice(playerJPADTO.getCurrentPrice());
        player.setPosition(playerJPADTO.getPosition());
        player.setTeam(playerJPADTO.getTeam() != null ? playerJPADTO.getTeam().aModelo() : null);
        player.setPlayerGameData(playerJPADTO.getPlayerGameData() != null ? playerJPADTO.getPlayerGameData().aModelo(player) : null);
        return player;
    }
}
