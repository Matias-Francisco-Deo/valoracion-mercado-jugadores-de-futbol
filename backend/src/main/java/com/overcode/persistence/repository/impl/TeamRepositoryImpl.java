package com.overcode.persistence.repository.impl;

import com.overcode.model.Player;
import com.overcode.model.Team;
import com.overcode.model.TeamUpsertResult;
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
    public Optional<Team> recuperarPorNombreYLiga(String name, String league) {
        return teamDAOJPA.findByNameAndLeague(name, league).map(TeamJPADTO::aModeloConJugadores);
    }

    @Override
    public TeamUpsertResult upsertTeam(Team team) {
        if (!teamDAOJPA.existsByNameAndLeague(team.getName(), team.getLeague())) {
            // si no existe el equipo, lo guarda junto a todos los jugadores
            Team savedTeam = teamDAOJPA.save(TeamJPADTO.desdeModelo(team)).aModeloConJugadores();
            return new TeamUpsertResult(savedTeam, savedTeam.getPlayers());
        }
        // si existe, actualizo sus datos (por ahora no tiene más) y actualizo sus jugadores

        List<Player> players = team.getPlayers();
        players.forEach(externalPlayerRepository::updatePlayerByExternalId);
        Optional<Team> optionalTeam = recuperarPorNombreYLiga(team.getName(), team.getLeague());
        if (optionalTeam.isEmpty()) return new TeamUpsertResult(team, List.of());

        Team teamToUpdate = optionalTeam.get();
        List<Player> newPlayers = players.stream()
                .filter(player -> !externalPlayerRepository.existsByExternalId(player))
                .toList();
        teamToUpdate.addPlayers(newPlayers);

        TeamJPADTO teamJPADTO = TeamJPADTO.desdeModelo(teamToUpdate);
        Team savedTeam = teamDAOJPA.save(teamJPADTO).aModeloConJugadores();

        List<Player> savedNewPlayers = savedTeam.getPlayers().stream()
                .filter(p -> newPlayers.stream().anyMatch(np ->
                        (np.getExternalId() != null && np.getExternalId().equals(p.getExternalId()))
                        || (np.getName() != null && np.getName().equals(p.getName()))
                ))
                .toList();

        return new TeamUpsertResult(savedTeam, savedNewPlayers);
    }
}
