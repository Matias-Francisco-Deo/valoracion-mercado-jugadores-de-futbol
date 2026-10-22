package com.overcode.persistence.repository.interfaces;

import com.overcode.model.player.Team;
import com.overcode.model.player.TeamUpsertResult;

import java.util.Optional;

public interface TeamRepository {

    Team guardar(Team team);

    Optional<Team> recuperar(Long id);

    Optional<Team> recuperarPorNombreYLiga(String name, String league);

    TeamUpsertResult upsertTeam(Team team);
}
