package com.overcode.persistence.repository.impl;

import com.overcode.model.PlayerGameData;
import com.overcode.persistence.dto.jpa.PlayerGameDataJPADTO;
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
        return Optional.empty();
    }
}
