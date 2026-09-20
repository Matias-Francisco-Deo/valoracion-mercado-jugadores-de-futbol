package com.overcode.persistence.repository.impl;

import com.overcode.model.Player;
import com.overcode.persistence.dto.external.PlayerDraftDTO;
import com.overcode.persistence.dto.jpa.PlayerJPADTO;
import com.overcode.persistence.repository.dao.external.ExternalDraftPlayerDAO;
import com.overcode.persistence.repository.dao.jpa.PlayerDAOJPA;
import com.overcode.persistence.repository.interfaces.ExternalPlayerRepository;
import com.overcode.service.interfaces.ExternalPlayerDataDAO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ExternalPlayerRepositoryImpl implements ExternalPlayerRepository {

    private final ExternalDraftPlayerDAO externalDraftPlayerDAO;
    private final PlayerDAOJPA playerDAOJPA;
    private final ExternalPlayerDataDAO externalPlayerDataDAO;

    public ExternalPlayerRepositoryImpl(ExternalDraftPlayerDAO externalDraftPlayerDAO, PlayerDAOJPA playerDAOJPA, ExternalPlayerDataDAO externalPlayerDataDAO) {
        this.externalDraftPlayerDAO = externalDraftPlayerDAO;
        this.playerDAOJPA = playerDAOJPA;
        this.externalPlayerDataDAO = externalPlayerDataDAO;
    }

    @Override
    public Optional<List<Player>> buscarYGuardarJugadores() {
        Optional<List<PlayerDraftDTO>> playerDraftDTOS = externalDraftPlayerDAO.listarJugadores();

        if (playerDraftDTOS.isEmpty()) {
            return Optional.empty();
        }

        List<Player> players = externalPlayerDataDAO.getDatosJugadores(playerDraftDTOS.get());

        playerDAOJPA.saveAll(players.stream().map(PlayerJPADTO::desdeModelo).toList());

        return Optional.of(players);

    }
}
