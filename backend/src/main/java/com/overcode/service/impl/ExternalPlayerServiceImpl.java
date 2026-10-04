package com.overcode.service.impl;

import com.overcode.model.Player;
import com.overcode.model.Team;
import com.overcode.persistence.dto.external.TeamDraftDTO;
import com.overcode.persistence.repository.interfaces.ExternalPlayerRepository;
import com.overcode.persistence.repository.interfaces.TeamRepository;
import com.overcode.service.interfaces.ExternalPlayerService;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class ExternalPlayerServiceImpl implements ExternalPlayerService {

    private final ExternalPlayerRepository externalPlayerRepository;
    private final TeamRepository teamRepository;

    public ExternalPlayerServiceImpl(ExternalPlayerRepository externalPlayerRepository, TeamRepository teamRepository) {
        this.externalPlayerRepository = externalPlayerRepository;

        this.teamRepository = teamRepository;
    }

    @Override
    @Transactional
    public Optional<List<Player>> actualizarJugadores(Integer maxTeams) {
        Optional<List<TeamDraftDTO>> teamDraftDTOS = externalPlayerRepository.listarEquiposDeJugadores(maxTeams);

        if (teamDraftDTOS.isEmpty()) return Optional.empty();

        Optional<List<Team>> optionalTeams = externalPlayerRepository.getDatosDeEquipos(teamDraftDTOS.get());

        if (optionalTeams.isEmpty()) return Optional.empty();

        List<Team> teams = optionalTeams.get().stream()
                .map(teamRepository::upsertTeam).toList();

        return Optional.of(teams.stream().flatMap(team -> team.getPlayers().stream()).toList());
    }


}
