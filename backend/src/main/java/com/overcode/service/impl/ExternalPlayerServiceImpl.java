package com.overcode.service.impl;

import com.overcode.model.Player;
import com.overcode.model.Team;
import com.overcode.persistence.dto.external.TeamDraftDTO;
import com.overcode.persistence.repository.interfaces.ExternalPlayerRepository;
import com.overcode.persistence.repository.interfaces.TeamRepository;
import com.overcode.service.interfaces.ExternalPlayerService;
import org.springframework.stereotype.Repository;

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
    public Optional<List<Player>> actualizarJugadores(Integer limit) {
        Optional<List<TeamDraftDTO>> teamDraftDTOS = externalPlayerRepository.listarEquiposDeJugadores(limit);

        if (teamDraftDTOS.isEmpty()) return Optional.empty();

        Optional<List<Team>> optionalTeams = externalPlayerRepository.getDatosDeEquipos(teamDraftDTOS.get());

        if (optionalTeams.isEmpty()) return Optional.empty();

        List<Team> teams = optionalTeams.get().stream()
                .map(teamRepository::upsertTeam).toList();

//        List<Player> upsertedPlayers = new java.util.ArrayList<>();
//        for (PlayerDraftDTO draftDTO : playerDraftDTOS.get()) {
//            Optional<Player> player = externalPlayerRepository.getDatosDeEquipos(draftDTO);
//            player.ifPresent(value -> upsertedPlayers
//                    .add(externalPlayerRepository.upsertPlayerByExternalId(value)));
//        }

//        if (upsertedPlayers.isEmpty()) return Optional.empty();

        return Optional.of(teams.stream().flatMap(team -> team.getPlayers().stream()).toList());
    }


}
