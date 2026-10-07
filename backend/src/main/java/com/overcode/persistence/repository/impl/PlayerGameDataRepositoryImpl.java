package com.overcode.persistence.repository.impl;

import com.overcode.model.Player;
import com.overcode.model.PlayerGameData;
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
    public Optional<PlayerGameData> recuperar(Long id) {
        return playerGameDataDAOJPA.findById(id)
                .map(dto -> {
                    Player player = dto.getPlayer().aModelo();
                    return dto.aModelo(player);
                });
    }


}
