package com.overcode.persistence.repository.impl;

import com.overcode.model.player.Player;
import com.overcode.model.player.PlayerGameData;
import com.overcode.model.player.Team;
import com.overcode.model.player.TeamUpsertResult;
import com.overcode.persistence.dto.jpa.PlayerGameDataJPADTO;
import com.overcode.persistence.dto.jpa.PlayerJPADTO;
import com.overcode.persistence.dto.jpa.TeamJPADTO;
import com.overcode.persistence.repository.dao.jpa.PlayerDAOJPA;
import com.overcode.persistence.repository.dao.jpa.TeamDAOJPA;
import com.overcode.persistence.repository.interfaces.TeamRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Repository
public class TeamRepositoryImpl implements TeamRepository {

    private final TeamDAOJPA teamDAOJPA;
    private final PlayerDAOJPA playerDAOJPA;

    public TeamRepositoryImpl(TeamDAOJPA teamDAOJPA, PlayerDAOJPA playerDAOJPA) {
        this.teamDAOJPA = teamDAOJPA;
        this.playerDAOJPA = playerDAOJPA;
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
    @Transactional
    public TeamUpsertResult upsertTeam(Team team) {
        TeamJPADTO targetTeam = teamDAOJPA.findByNameAndLeague(team.getName(), team.getLeague())
                .orElseGet(() -> teamDAOJPA.save(TeamJPADTO.desdeModeloSinJugadores(team)));

        List<Player> incomingPlayers = deduplicatePlayers(team.getPlayers());
        List<Player> addedPlayers = new ArrayList<>();

        for (Player incoming : incomingPlayers) {
            Optional<PlayerJPADTO> existingOpt = findExistingPlayer(incoming);
            if (existingOpt.isPresent()) {
                reconcileExistingPlayer(existingOpt.get(), incoming, targetTeam);
            } else {
                targetTeam.getPlayers().add(PlayerJPADTO.desdeModelo(incoming, targetTeam));
                addedPlayers.add(incoming);
            }
        }

        Team savedTeam = teamDAOJPA.save(targetTeam).aModeloConJugadores();
        List<Player> savedNewPlayers = matchSavedNewPlayers(savedTeam.getPlayers(), addedPlayers);

        return new TeamUpsertResult(savedTeam, savedNewPlayers);
    }

    private Optional<PlayerJPADTO> findExistingPlayer(Player incoming) {
        if (incoming.getExternalId() != null) {
            Optional<PlayerJPADTO> byExternalId = playerDAOJPA.findByExternalId(incoming.getExternalId());
            if (byExternalId.isPresent()) {
                return byExternalId;
            }
        }
        if (incoming.getName() != null) {
            return playerDAOJPA.findFirstByNameIgnoreCaseAndExternalIdIsNull(incoming.getName());
        }
        return Optional.empty();
    }

    private void reconcileExistingPlayer(PlayerJPADTO existing, Player incoming, TeamJPADTO targetTeam) {
        if (existing.getExternalId() == null && incoming.getExternalId() != null) {
            existing.setExternalId(incoming.getExternalId());
        }

        updateGameData(existing, incoming.getPlayerGameData());

        if (existing.getTeam() == null || !existing.getTeam().getId().equals(targetTeam.getId())) {
            existing.setTeam(targetTeam);
        }
        if (targetTeam.getPlayers().stream().noneMatch(p -> existing.getId() != null && existing.getId().equals(p.getId()))) {
            targetTeam.getPlayers().add(existing);
        }
    }

    private void updateGameData(PlayerJPADTO existing, PlayerGameData incomingData) {
        if (incomingData == null) {
            return;
        }
        PlayerGameDataJPADTO gameData = existing.getPlayerGameData();
        if (gameData == null) {
            existing.setPlayerGameData(PlayerGameDataJPADTO.desdeModelo(incomingData, existing));
            return;
        }
        gameData.setGoals(incomingData.getGoals());
        gameData.setAssists(incomingData.getAssists());
        gameData.setShotsOnTarget(incomingData.getShotsOnTarget());
        gameData.setTackles(incomingData.getTackles());
        gameData.setKeyPasses(incomingData.getKeyPasses());
        gameData.setRating(incomingData.getRating());
        gameData.setSuccessfulDribbles(incomingData.getSuccessfulDribbles());
        gameData.setPosition(incomingData.getPosition());
    }

    private List<Player> deduplicatePlayers(List<Player> players) {
        if (players == null || players.isEmpty()) {
            return List.of();
        }
        List<Player> deduplicated = new ArrayList<>();
        Set<Long> seenExternalIds = new HashSet<>();
        Set<String> seenNamesWithoutExternalId = new HashSet<>();

        for (Player player : players) {
            if (player.getExternalId() != null) {
                if (seenExternalIds.add(player.getExternalId())) {
                    deduplicated.add(player);
                }
            } else {
                String normalizedName = player.getName() == null ? null : player.getName().trim().toLowerCase(Locale.ROOT);
                if (normalizedName == null || seenNamesWithoutExternalId.add(normalizedName)) {
                    deduplicated.add(player);
                }
            }
        }
        return deduplicated;
    }

    private List<Player> matchSavedNewPlayers(List<Player> savedPlayers, List<Player> addedPlayers) {
        return savedPlayers.stream()
                .filter(p -> addedPlayers.stream().anyMatch(np ->
                        (np.getExternalId() != null && np.getExternalId().equals(p.getExternalId()))
                        || (np.getExternalId() == null && p.getExternalId() == null
                            && np.getName() != null && np.getName().equals(p.getName()))
                ))
                .toList();
    }
}
