package com.overcode.persistence.repository.dao.external;

import com.overcode.model.Player;
import com.overcode.persistence.dto.external.PlayerDraftDTO;
import com.overcode.persistence.repository.dao.external.scrapper.whoscored.ExternalPlayerWhoScoredScrapper;
import com.overcode.persistence.repository.dao.external.scrapper.whoscored.WhoScoredIdResolver;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ExternalPlayerDAOWhoScoredImpl implements ExternalPlayerDataDAO {

    private final WhoScoredIdResolver whoScoredIdResolver;
    private final ExternalPlayerWhoScoredScrapper externalPlayerWhoScoredScrapper;

    public ExternalPlayerDAOWhoScoredImpl(WhoScoredIdResolver whoScoredIdResolver,
                                          ExternalPlayerWhoScoredScrapper externalPlayerWhoScoredScrapper) {
        this.whoScoredIdResolver = whoScoredIdResolver;
        this.externalPlayerWhoScoredScrapper = externalPlayerWhoScoredScrapper;
    }

//    @Cacheable(value = "playerMetricsCache", key = "#teamName + '-' + #playerName")
    @Override
    public Optional<Player> getDatosDeJugador(PlayerDraftDTO playerDraftDTO) {
        try {
            Long playerId = whoScoredIdResolver.resolvePlayerId(playerDraftDTO.clubName(), playerDraftDTO.name());
            return externalPlayerWhoScoredScrapper.getDatosDeJugador(playerId, playerDraftDTO);
        } catch (Exception e) {
            System.err.println("Saltando jugador " + playerDraftDTO.name() + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<Player> getDatosJugadores(List<PlayerDraftDTO> playerDraftDTOS) {
        return playerDraftDTOS.stream()
                .map(this::getDatosDeJugador)
                .flatMap(Optional::stream)
                .toList();
    }
}
