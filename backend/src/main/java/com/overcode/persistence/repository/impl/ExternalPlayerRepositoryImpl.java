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
    static private final Integer MAX_PLAYERS_TO_RETRIEVE = null;

    public ExternalPlayerRepositoryImpl(ExternalDraftPlayerDAO externalDraftPlayerDAO, PlayerDAOJPA playerDAOJPA, ExternalPlayerDataDAO externalPlayerDataDAO) {
        this.externalDraftPlayerDAO = externalDraftPlayerDAO;
        this.playerDAOJPA = playerDAOJPA;
        this.externalPlayerDataDAO = externalPlayerDataDAO;
    }

    @Override
    public Optional<List<Player>> buscarYGuardarJugadores() {
        Optional<List<PlayerDraftDTO>> playerDraftDTOS = externalDraftPlayerDAO.listarJugadores(MAX_PLAYERS_TO_RETRIEVE);

        if (playerDraftDTOS.isEmpty()) return Optional.empty();

        List<PlayerJPADTO> upsertedPlayers = new java.util.ArrayList<>();
        for (PlayerDraftDTO draftDTO : playerDraftDTOS.get()) {
            Optional<Player> player = externalPlayerDataDAO.getDatosDeJugador(draftDTO);
            if (player.isPresent()) {
                upsertedPlayers.add(upsertPlayer(player.get()));
            }
        }

        if (upsertedPlayers.isEmpty()) return Optional.empty();

        return Optional.of(upsertedPlayers.stream().map(PlayerJPADTO::aModelo).toList());
    }

    private PlayerJPADTO upsertPlayer(Player player) {
        boolean existsOnDB = player.getExternalId() != null && playerDAOJPA.existsByExternalId(player.getExternalId());
        if (!existsOnDB) {
            return playerDAOJPA.save(PlayerJPADTO.desdeModelo(player));
        }
        playerDAOJPA.updateWithExternalId(
                player.getExternalId(),
                player.getName(),
                player.getGoals(),
                player.getCurrentPrice(),
                player.getAssists(),
                player.getClubName(),
                player.getShotsOnTarget(),
                player.getPasses(),
                player.getInterceptions(),
                player.getTackles(),
                player.getKeyPasses(),
                player.getRating(),
                player.getSuccessfulDribbles()
        );
        Optional<PlayerJPADTO> optionalPlayerJPADTO = playerDAOJPA.findByExternalId(player.getExternalId());

        if (optionalPlayerJPADTO.isEmpty()) return
                PlayerJPADTO.desdeModelo(player);

        return optionalPlayerJPADTO.get();
    }
}
