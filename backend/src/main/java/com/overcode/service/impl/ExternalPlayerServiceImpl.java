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
    private static final Logger log = LoggerFactory.getLogger(ExternalPlayerServiceImpl.class);

    public ExternalPlayerServiceImpl(ExternalPlayerRepository externalPlayerRepository, TeamRepository teamRepository) {
        this.externalPlayerRepository = externalPlayerRepository;

        this.teamRepository = teamRepository;
    }

    @Override
    public Optional<List<Player>> actualizarJugadores(Integer maxTeams) {
        Optional<List<TeamDraftDTO>> teamDraftDTOS = externalPlayerRepository.listarEquiposDeJugadores(maxTeams);

        if (teamDraftDTOS.isEmpty()) return Optional.empty();

        Optional<List<Team>> optionalTeams = externalPlayerRepository.getDatosDeEquipos(teamDraftDTOS.get());

        if (optionalTeams.isEmpty()) return Optional.empty();

        List<Team> teams = optionalTeams.get().stream()
                .map(teamRepository::upsertTeam).toList();

        return Optional.of(teams.stream().flatMap(team -> team.getPlayers().stream()).toList());
    }

    @Override
    @Async
    public CompletableFuture<Void> actualizarJugadoresAsync() {
        log.info("Iniciando actualizacion asincrona manual...");
        actualizarJugadores(null);
        log.info("Finalizo la actualizacion asincrona manual.");
        return CompletableFuture.completedFuture(null);
    }
}
