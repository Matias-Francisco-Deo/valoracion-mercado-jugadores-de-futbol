package com.overcode.persistence.dto.jpa;

import com.overcode.model.Team;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity(name="team")
@Table(name = "teams")
@Setter
@Getter
@NoArgsConstructor
public class TeamJPADTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String league;

    @Column(nullable = false)
    @OneToMany(mappedBy = "team")
    private List<PlayerJPADTO> players;


    public TeamJPADTO(Long id, String name, String league, List<PlayerJPADTO> players) {
        setId(id);
        setName(name);
        setLeague(league);
        setPlayers(players);
    }

    public static TeamJPADTO desdeModelo(Team team) {
        if (team == null) {
            return null;
        }
        TeamJPADTO dto = new TeamJPADTO();
        dto.setId(team.getId());
        dto.setName(team.getName());
        dto.setLeague(team.getLeague());
        dto.setPlayers(team.getPlayers().stream().map(player -> PlayerJPADTO.desdeModelo(player, dto)).toList());
        return dto;
    }

    public Team aModelo() {
        Team team = new Team();
        team.setId(this.id);
        team.setName(this.name);
        team.setLeague(this.league);
        team.setPlayers(this.players.stream().map(playerJPADTO -> playerJPADTO.aModelo(team)).toList());
        return team;
    }
}
