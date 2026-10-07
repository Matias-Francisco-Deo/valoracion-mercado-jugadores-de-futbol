package com.overcode.persistence.repository.interfaces;

import com.overcode.model.Team;

import java.util.Optional;

public interface TeamRepository {

    Team guardar(Team team);

    Optional<Team> recuperar(Long id);

    Optional<Team> recuperarPorNombreYLiga(String name, String league);

    Team upsertTeam(Team team);
}
