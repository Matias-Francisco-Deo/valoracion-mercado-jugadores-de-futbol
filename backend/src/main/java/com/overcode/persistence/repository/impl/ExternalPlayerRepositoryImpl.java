package com.overcode.persistence.repository.impl;

import com.overcode.model.Player;
import com.overcode.persistence.dto.external.PlayerDraftDTO;
import com.overcode.persistence.dto.jpa.PlayerJPADTO;
import com.overcode.persistence.repository.dao.external.ExternalDraftPlayerDAO;
import com.overcode.persistence.repository.dao.external.ExternalPlayerDataDAO;
import com.overcode.persistence.repository.dao.jpa.PlayerDAOJPA;
import com.overcode.persistence.repository.interfaces.ExternalPlayerRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ExternalPlayerRepositoryImpl implements ExternalPlayerRepository {

    private final ExternalDraftPlayerDAO externalDraftPlayerDAO;
    private final PlayerDAOJPA playerDAOJPA;
    private final ExternalPlayerDataDAO externalPlayerDataDAO;
    private final Integer MAX_PLAYERS_TO_RETRIEVE = 20;

    public ExternalPlayerRepositoryImpl(ExternalDraftPlayerDAO externalDraftPlayerDAO, PlayerDAOJPA playerDAOJPA, ExternalPlayerDataDAO externalPlayerDataDAO) {
        this.externalDraftPlayerDAO = externalDraftPlayerDAO;
        this.playerDAOJPA = playerDAOJPA;
        this.externalPlayerDataDAO = externalPlayerDataDAO;
    }

    @Override
    public Optional<List<Player>> buscarYGuardarJugadores() {
        Optional<List<PlayerDraftDTO>> playerDraftDTOS = externalDraftPlayerDAO.listarJugadores(MAX_PLAYERS_TO_RETRIEVE);

        if (playerDraftDTOS.isEmpty()) return Optional.empty();


        Optional<List<Player>> players = externalPlayerDataDAO.getDatosJugadores(playerDraftDTOS.get());

        if (players.isEmpty()) return Optional.empty();

        playerDAOJPA.saveAll(players.get().stream().map(PlayerJPADTO::desdeModelo).toList());

        return players;

    }
}
