package com.overcode.service.impl;

import com.overcode.model.Player;
import com.overcode.model.Team;
import com.overcode.persistence.dto.external.TeamDraftDTO;
import com.overcode.persistence.repository.interfaces.ExternalPlayerRepository;
import com.overcode.persistence.repository.interfaces.TeamRepository;
import com.overcode.service.interfaces.ExternalPlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class ExternalPlayerServiceImpl implements ExternalPlayerService {

    private final ExternalPlayerRepository externalPlayerRepository;
    private final TeamRepository teamRepository;
    private final com.overcode.service.interfaces.TokenEmissionService tokenEmissionService;
    private static final Logger log = LoggerFactory.getLogger(ExternalPlayerServiceImpl.class);

    public ExternalPlayerServiceImpl(ExternalPlayerRepository externalPlayerRepository, TeamRepository teamRepository, com.overcode.service.interfaces.TokenEmissionService tokenEmissionService) {
        this.externalPlayerRepository = externalPlayerRepository;
        this.teamRepository = teamRepository;
        this.tokenEmissionService = tokenEmissionService;
    }

    @Override
    public Optional<List<Player>> actualizarJugadores(Integer maxTeams) {
        Optional<List<TeamDraftDTO>> teamDraftDTOS = externalPlayerRepository.listarEquiposDeJugadores(maxTeams);

        if (teamDraftDTOS.isEmpty()) return Optional.empty();

        Optional<List<Team>> optionalTeams = externalPlayerRepository.getDatosDeEquipos(teamDraftDTOS.get());

        if (optionalTeams.isEmpty()) return Optional.empty();

        List<Team> teams = optionalTeams.get().stream()
                .map(teamRepository::upsertTeam).toList();

        List<Player> allPlayers = teams.stream().flatMap(team -> team.getPlayers().stream()).toList();
        
        // Emit tokens for all players (method is idempotent, so it won't emit twice for existing players)
        allPlayers.forEach(tokenEmissionService::emitTokensForNewPlayer);

        return Optional.of(allPlayers);
    }

    @Override
    @Async
    public CompletableFuture<Void> actualizarJugadoresAsync(Integer limit) {
        log.info("Iniciando actualizacion asincrona manual con limite: {}", limit);
        try {
            actualizarJugadores(limit);
            log.info("Finalizó la actualización asíncrona manual.");
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            // Without this, the exception is stored in the discarded CompletableFuture and silently swallowed,
            // which makes the async task look "stuck" instead of reporting what actually failed.
            log.error("Falló la actualización asíncrona manual: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
}
