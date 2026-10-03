package com.overcode.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Team {
    private Long id;

    private String name;
    private String league;

    private List<Player> players = new ArrayList<>();

    public Team(Long id, String name, String league) {
        setId(id);
        setName(name);
        setLeague(league);
    }

    public Team(String name, String league) {
        setId(null);
        setName(name);
        setLeague(league);
    }

    public Team(String name, String league, List<Player> players) {
        setId(null);
        setName(name);
        setLeague(league);
        setPlayers(players);
    }

    public void addPlayer(Player player) {
        players.add(player);
        player.setTeam(this);
    }

    public void removePlayer(Player player) {
        players.remove(player);
        player.setTeam(null);
    }
}
