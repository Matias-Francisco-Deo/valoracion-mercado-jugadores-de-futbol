package com.overcode.persistence.repository.impl;

import com.overcode.model.Player;
import com.overcode.persistence.dto.external.PlayerDraftDTO;
import com.overcode.persistence.repository.dao.external.ExternalPlayerDAOFootballDataAPI;
import com.overcode.persistence.repository.dao.jpa.PlayerDAOJPA;
import com.overcode.persistence.repository.interfaces.ExternalPlayerRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExternalPlayerRepositoryImpl implements ExternalPlayerRepository {

    private final ExternalPlayerDAOFootballDataAPI externalPlayerDAOFootballDataAPI;
    private final PlayerDAOJPA playerDAOJPA;

    public ExternalPlayerRepositoryImpl(ExternalPlayerDAOFootballDataAPI externalPlayerDAOFootballDataAPI, PlayerDAOJPA playerDAOJPA) {
        this.externalPlayerDAOFootballDataAPI = externalPlayerDAOFootballDataAPI;
        this.playerDAOJPA = playerDAOJPA;
    }

    @Override
    public List<Player> buscarYGuardarJugadores() {
        List<PlayerDraftDTO> playerDraftDTOS = externalPlayerDAOFootballDataAPI.listarJugadores();
        playerDAOJPA.saveAll(playerDraftDTOS);
        return playerDraftDTOS;

    }
}
