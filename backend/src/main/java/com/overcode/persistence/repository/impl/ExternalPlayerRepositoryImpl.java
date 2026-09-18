package com.overcode.persistence.repository.impl;

import com.overcode.model.Player;
import com.overcode.persistence.dto.external.FootballDataAPI.FootballDataPlayerDraftDTO;
import com.overcode.persistence.repository.dao.external.ExternalPlayerDAOFootballDataAPI;
import com.overcode.persistence.repository.dao.jpa.PlayerDAOJPA;
import com.overcode.persistence.repository.interfaces.ExternalPlayerRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ExternalPlayerRepositoryImpl implements ExternalPlayerRepository {

    private final ExternalPlayerDAOFootballDataAPI externalPlayerDAOFootballDataAPI;
    private final PlayerDAOJPA playerDAOJPA;

    public ExternalPlayerRepositoryImpl(ExternalPlayerDAOFootballDataAPI externalPlayerDAOFootballDataAPI, PlayerDAOJPA playerDAOJPA) {
        this.externalPlayerDAOFootballDataAPI = externalPlayerDAOFootballDataAPI;
        this.playerDAOJPA = playerDAOJPA;
    }

    @Override
    public Optional<List<Player>> buscarYGuardarJugadores() {
        Optional<List<FootballDataPlayerDraftDTO>> playerDraftDTOS = externalPlayerDAOFootballDataAPI.listarJugadores();

        if (playerDraftDTOS.isEmpty()) {
            return Optional.empty();
        }


//        playerDAOJPA.saveAll(playerDraftDTOS);
        return Optional.of(List.of());

    }
}
