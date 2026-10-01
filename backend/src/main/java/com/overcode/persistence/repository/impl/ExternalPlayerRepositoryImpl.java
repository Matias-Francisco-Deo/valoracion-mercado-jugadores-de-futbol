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

import org.springframework.transaction.annotation.Transactional;

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
                player.getSuccessfulDribbles(),
                player.getPosition()
        );
        Optional<PlayerJPADTO> optionalPlayerJPADTO = playerDAOJPA.findByExternalId(player.getExternalId());

        if (optionalPlayerJPADTO.isEmpty()) return player;

        return optionalPlayerJPADTO.get().aModelo();
    }
}
