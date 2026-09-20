package com.overcode.persistence.repository.dao.external;

import com.overcode.model.Player;
import com.overcode.persistence.dto.external.PlayerDraftDTO;
import com.overcode.persistence.repository.dao.external.scraper.service.WhoScoredIdResolver;
import com.overcode.service.impl.ExternalPlayerWhoScoredScrapper;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    public Player getDatosDeJugador(PlayerDraftDTO playerDraftDTO) {
        Long playerId = whoScoredIdResolver.resolvePlayerId(playerDraftDTO.clubName(), playerDraftDTO.name());

        return externalPlayerWhoScoredScrapper.getDatosDeJugador(playerId, playerDraftDTO);
    }

    @Override
    public List<Player> getDatosJugadores(List<PlayerDraftDTO> playerDraftDTOS) {
        return playerDraftDTOS.stream()
                .map(this::getDatosDeJugador)
                .toList();
    }
}
