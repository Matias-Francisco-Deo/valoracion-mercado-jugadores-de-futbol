package com.overcode.testUtils;

import com.overcode.model.Player;
import com.overcode.model.Team;

import java.util.List;

public class TestTeamUtil {
    public static Team getTeam() {
        return getTeamConNombreYLiga("Club", "Liga1");
    }

    public static Team getTeamConNombreYLiga(String name, String league) {
        return new Team(name, league);
    }

    public static Team getTeamConJugadores(List<Player> players) {
        return new Team("Club", "Liga1", players);
    }
}
