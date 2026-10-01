package com.overcode.testUtils;

import com.overcode.model.Player;
import com.overcode.model.PlayerGameData;
import com.overcode.model.Team;
import org.jspecify.annotations.NonNull;

public class TestPlayerUtil {
    public static Player getJugadorConRating(String name, Double rating) {
        return new Player(name,
                getTeam(),
                getPlayerDataConRating(rating));
    }

    private static @NonNull Team getTeam() {
        return new Team("Club", "Liga1");
    }

    private static @NonNull PlayerGameData getPlayerDataConRating(Double rating) {
        return new PlayerGameData(1L, 0, 0, 0, 0, 0, 0, rating);
    }

    public static Player getJugadorConClub(String name, String club) {
        return new Player(name, getTeamConNombre(club), getPlayerData());
    }

    private static @NonNull Team getTeamConNombre(String name) {
        return new Team(name, "Liga1");
    }

    private static @NonNull PlayerGameData getPlayerData() {
        return new PlayerGameData(1L, 0, 0, 0, 0, 0, 0, 1.0);
    }

    public static Player getJugadorConLiga(String name, String liga) {
        return new Player(name, getTeamConLiga(liga), getPlayerData());
    }

    private static @NonNull Team getTeamConLiga(String league) {
        return new Team("Club1", league);
    }

    public static Player getJugadorConLigaYClub(String name, String liga, String clubName) {
        Player jugador = getJugadorConLiga(name, liga);
        jugador.getTeam().setName(clubName);
        return jugador;
    }

    public static Player getJugadorConNombre(String name) {
        return new Player(name, getTeam(), getPlayerData());
    }
}
