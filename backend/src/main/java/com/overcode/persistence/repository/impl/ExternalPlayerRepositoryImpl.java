package com.overcode.persistence.repository.impl;

import com.overcode.model.Player;
import com.overcode.model.PlayerGameData;
import com.overcode.persistence.dto.external.PlayerDraftDTO;
import com.overcode.persistence.dto.jpa.PlayerJPADTO;
import com.overcode.persistence.repository.dao.external.ExternalDraftPlayerDAO;
import com.overcode.persistence.repository.dao.external.ExternalPlayerDataDAO;
import com.overcode.persistence.repository.dao.jpa.PlayerDAOJPA;
import com.overcode.persistence.repository.dao.jpa.PlayerGameDataDAOJPA;
import com.overcode.persistence.repository.interfaces.ExternalPlayerRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class ExternalPlayerRepositoryImpl implements ExternalPlayerRepository {

    private final ExternalDraftPlayerDAO externalDraftPlayerDAO;
    private final PlayerDAOJPA playerDAOJPA;
    private final PlayerGameDataDAOJPA playerGameDataDAOJPA;
    private final ExternalPlayerDataDAO externalPlayerDataDAO;

    public ExternalPlayerRepositoryImpl(ExternalDraftPlayerDAO externalDraftPlayerDAO, PlayerDAOJPA playerDAOJPA, PlayerGameDataDAOJPA playerGameDataDAOJPA, ExternalPlayerDataDAO externalPlayerDataDAO) {
        this.externalDraftPlayerDAO = externalDraftPlayerDAO;
        this.playerDAOJPA = playerDAOJPA;
        this.playerGameDataDAOJPA = playerGameDataDAOJPA;
        this.externalPlayerDataDAO = externalPlayerDataDAO;
    }

    @Override
    public Optional<List<PlayerDraftDTO>> listarJugadores(Integer maxPlayers) {
        return externalDraftPlayerDAO.listarJugadores(maxPlayers);
    }

    @Override
    public Optional<Player> getDatosDeJugador(PlayerDraftDTO playerDraftDTO) {
        return externalPlayerDataDAO.getDatosDeJugador(playerDraftDTO);
    }

    @Override
    @Transactional
    public Player upsertPlayerByExternalId(Player player) {
        boolean existsOnDB = player.getExternalId() != null && playerDAOJPA.existsByExternalId(player.getExternalId());
        if (!existsOnDB) {
            return playerDAOJPA.save(PlayerJPADTO.desdeModelo(player)).aModelo();
        }

        PlayerGameData playerGameData = player.getPlayerGameData();
        playerGameDataDAOJPA.updateWithExternalPlayerId(
                player.getExternalId(),
                playerGameData.getGoals(),
                playerGameData.getAssists(),
                playerGameData.getShotsOnTarget(),
                playerGameData.getTackles(),
                playerGameData.getKeyPasses(),
                playerGameData.getRating(),
                playerGameData.getSuccessfulDribbles()
        );

        Optional<PlayerJPADTO> optionalPlayerJPADTO = playerDAOJPA.findByExternalId(player.getExternalId());

        if (optionalPlayerJPADTO.isEmpty()) return player;

        return optionalPlayerJPADTO.get().aModelo();
    }
}
