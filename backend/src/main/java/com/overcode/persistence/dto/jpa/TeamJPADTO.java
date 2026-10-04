package com.overcode.persistence.dto.jpa;

import com.overcode.model.Team;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity(name="team")
@Table(name = "teams", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "league"})})
@Setter
@Getter
@NoArgsConstructor
public class TeamJPADTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String league;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerJPADTO> players = new ArrayList<>();

    public TeamJPADTO(Long id, String name, String league) {
        setId(id);
        setName(name);
        setLeague(league);
    }

    public static TeamJPADTO desdeModelo(Team team) {
        if (team == null) {
            return null;
        }
        TeamJPADTO dto = new TeamJPADTO();
        dto.setId(team.getId());
        dto.setName(team.getName());
        dto.setLeague(team.getLeague());
        
        if (team.getPlayers() != null) {
            dto.setPlayers(team.getPlayers().stream()
                    .map(p -> PlayerJPADTO.desdeModelo(p, dto))
                    .toList());
        }
        return dto;
    }

    public static TeamJPADTO desdeModeloSinJugadores(Team team) {
        if (team == null) {
            return null;
        }
        TeamJPADTO dto = new TeamJPADTO();
        dto.setId(team.getId());
        dto.setName(team.getName());
        dto.setLeague(team.getLeague());
        return dto;
    }

    public Team aModelo() {
        Team team = new Team();
        team.setId(this.id);
        team.setName(this.name);
        team.setLeague(this.league);
        return team;
    }

    public Team aModeloConJugadores() {
        Team team = new Team();
        team.setId(this.id);
        team.setName(this.name);
        team.setLeague(this.league);
        if (this.players != null) {
            team.setPlayers(this.players.stream()
                    .map(p -> p.aModelo(team))
                    .toList());
        }
        return team;
    }

    public void addPlayer(PlayerJPADTO player) {
        players.add(player);
        player.setTeam(this);
    }

    public void removePlayer(PlayerJPADTO player) {
        players.remove(player);
        player.setTeam(null);
    }
}
