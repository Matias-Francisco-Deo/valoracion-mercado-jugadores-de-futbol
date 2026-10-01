package com.overcode.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Team {
    private Long id;

    private String name;
    private String league;

//    private List<Player> players;

    public Team(Long id, String name, String league) {
        setId(id);
        setName(name);
        setLeague(league);
//        setPlayers(players);
    }

    public Team(String name, String league) {
        setId(null);
        setName(name);
        setLeague(league);
//        setPlayers(players);
    }
}
