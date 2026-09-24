package com.overcode.persistence.repository.dao.external;

import com.overcode.model.Player;
import com.overcode.persistence.dto.external.PlayerDraftDTO;
import com.overcode.persistence.repository.dao.external.scrapper.whoscored.ExternalPlayerWhoScoredScrapper;
import com.overcode.persistence.repository.dao.external.scrapper.whoscored.WhoScoredIdResolver;
import com.overcode.service.impl.AuthServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ExternalPlayerDAOWhoScoredImpl implements ExternalPlayerDataDAO {

    private final WhoScoredIdResolver whoScoredIdResolver;
    private final ExternalPlayerWhoScoredScrapper externalPlayerWhoScoredScrapper;

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    public ExternalPlayerDAOWhoScoredImpl(WhoScoredIdResolver whoScoredIdResolver,
                                          ExternalPlayerWhoScoredScrapper externalPlayerWhoScoredScrapper) {
        this.whoScoredIdResolver = whoScoredIdResolver;
        this.externalPlayerWhoScoredScrapper = externalPlayerWhoScoredScrapper;
    }

    @Override
    public Optional<Player> getDatosDeJugador(PlayerDraftDTO playerDraftDTO) {
        try {
            Long playerId = whoScoredIdResolver.resolvePlayerId(playerDraftDTO.clubName(), playerDraftDTO.name());
            log.info("Buscando jugador: {}", playerDraftDTO.name());
            return externalPlayerWhoScoredScrapper.getDatosDeJugador(playerId, playerDraftDTO);
        } catch (Exception e) { // TODO excepcion muy general?
            log.error("Saltando jugador {}: {}", playerDraftDTO.name(), e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<Player>> getDatosJugadores(List<PlayerDraftDTO> playerDraftDTOS) {
        return Optional.of(playerDraftDTOS.stream().map(this::getDatosDeJugador).flatMap(Optional::stream).toList());
    }
}
