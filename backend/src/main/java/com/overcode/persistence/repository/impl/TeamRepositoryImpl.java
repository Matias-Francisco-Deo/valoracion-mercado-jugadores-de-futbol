package com.overcode.persistence.repository.impl;

import com.overcode.model.Player;
import com.overcode.model.Team;
import com.overcode.persistence.dto.jpa.TeamJPADTO;
import com.overcode.persistence.repository.dao.jpa.TeamDAOJPA;
import com.overcode.persistence.repository.interfaces.ExternalPlayerRepository;
import com.overcode.persistence.repository.interfaces.TeamRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TeamRepositoryImpl implements TeamRepository {

    private final TeamDAOJPA teamDAOJPA;
    private final ExternalPlayerRepository externalPlayerRepository;

    public TeamRepositoryImpl(TeamDAOJPA teamDAOJPA, ExternalPlayerRepository externalPlayerRepository) {
        this.teamDAOJPA = teamDAOJPA;
        this.externalPlayerRepository = externalPlayerRepository;
    }

    @Override
    public Team guardar(Team team) {
        TeamJPADTO dto = TeamJPADTO.desdeModelo(team);
        return teamDAOJPA.save(dto).aModeloConJugadores();
    }

    @Override
    public Optional<Team> recuperar(Long id) {
        return teamDAOJPA.findById(id).map(TeamJPADTO::aModeloConJugadores);
    }

    @Override
    public Optional<Team> recuperarPorNombre(String name) {
        return teamDAOJPA.findByName(name).map(TeamJPADTO::aModeloConJugadores);
    }

    @Override
    public Optional<Team> recuperarPorNombreYLiga(String name, String league) {
        return teamDAOJPA.findByNameAndLeague(name, league).map(TeamJPADTO::aModeloConJugadores);
    }

    @Override
    public Team upsertTeam(Team team) {
        if (!teamDAOJPA.existsByNameAndLeague(team.getLeague(), team.getName())) {
            // si no existe el equipo, lo guarda junto a todos los jugadores
            return teamDAOJPA.save(TeamJPADTO.desdeModelo(team)).aModelo();
        }
        // si existe, actualizo sus datos (por ahora no tiene más) y actualizo sus jugadores

        Team teamPersistido = teamDAOJPA.findByNameAndLeague(team.getName(), team.getLeague()).get().aModelo();
        List<Player> updatedPlayers = team.getPlayers().stream().map(externalPlayerRepository::updatePlayerByExternalId).toList();
        teamPersistido.setPlayers(updatedPlayers);
        // guardo al team con todos los datos actualizados

        return teamDAOJPA.save(TeamJPADTO.desdeModelo(teamPersistido)).aModelo();
    }
}
