package com.overcode.persistence.repository.dao.external;

import com.overcode.model.player.Player;
import com.overcode.model.player.Team;
import com.overcode.persistence.dto.external.PlayerDraftDTO;
import com.overcode.persistence.dto.external.TeamDraftDTO;
import com.overcode.persistence.repository.dao.external.scraper.whoscored.WhoScoredIdResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ExternalPlayerDAOWhoScoredImpl implements ExternalPlayerDataDAO {

    private final WhoScoredIdResolver whoScoredIdResolver;
    private final com.overcode.persistence.repository.dao.external.scraper.whoscored.ExternalPlayerWhoScoredScraper externalPlayerWhoScoredscraper;

    private static final Logger log = LoggerFactory.getLogger(ExternalPlayerDAOWhoScoredImpl.class);

    public ExternalPlayerDAOWhoScoredImpl(WhoScoredIdResolver whoScoredIdResolver,
                                          com.overcode.persistence.repository.dao.external.scraper.whoscored.ExternalPlayerWhoScoredScraper externalPlayerWhoScoredscraper) {
        this.whoScoredIdResolver = whoScoredIdResolver;
        this.externalPlayerWhoScoredscraper = externalPlayerWhoScoredscraper;
    }

    @Override
    public Optional<List<Team>> getDatosDeEquipos(List<TeamDraftDTO> teams) {
        return Optional.of(teams.stream().map(this::getDatosDeEquipo)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList());
    }

    private Optional<Team> getDatosDeEquipo(TeamDraftDTO team) {
        Optional<List<Player>> optionalPlayers = getDatosJugadores(team.players());
        return optionalPlayers.map(team::aModelo);
    }

    private Optional<Player> getDatosDeJugador(PlayerDraftDTO player) {
        try {
            Long playerId;

            playerId = whoScoredIdResolver.resolvePlayerId(player.name());
            log.info("Buscando nuevo jugador: {}", player.name());


            return externalPlayerWhoScoredscraper.getDatosDeJugador(playerId, player);
        } catch (Exception e) {
            log.error("Saltando jugador {}: {}", player.name(), e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<List<Player>> getDatosJugadores(List<PlayerDraftDTO> playerDraftDTOS) {
        return Optional.of(playerDraftDTOS.stream().map(this::getDatosDeJugador).flatMap(Optional::stream).toList());
    }
}
