package com.overcode.testUtils;

import com.overcode.model.Player;
import com.overcode.model.PlayerGameData;
import com.overcode.model.Team;
import org.jspecify.annotations.NonNull;

public class TestPlayerUtil {
    public static Player getJugadorConRating(String name, Double rating) {
        Player player = new Player(name, getTeam(), null);
        PlayerGameData playerGameData = getPlayerDataConRating(rating, player);
        player.setPlayerGameData(playerGameData);
        return player;
    }

    public static @NonNull Team getTeam() {
        return new Team("Club", "Liga1");
    }

    private static @NonNull PlayerGameData getPlayerDataConRating(Double rating, Player player) {
        return new PlayerGameData(null, player, 0, 0, 0, 0, 0, 0, rating);
    }

    public static Player getJugadorConClub(String name, String club) {
        Player player = new Player(name, getTeamConNombre(club), null);
        PlayerGameData playerGameData = getPlayerData(player);
        player.setPlayerGameData(playerGameData);
        return player;
    }

    private static @NonNull Team getTeamConNombre(String name) {
        return new Team(name, "Liga1");
    }

    public static @NonNull PlayerGameData getPlayerData(Player player) {
        return new PlayerGameData(null, player, 0, 0, 0, 0, 0, 0, 1.0);
    }

    public static Player getJugadorConLiga(String name, String liga) {
        Player player = new Player(name, getTeamConLiga(liga), null);
        PlayerGameData playerGameData = getPlayerData(player);
        player.setPlayerGameData(playerGameData);

        return player;
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
        Player player = new Player(name, getTeam(), null);
        PlayerGameData playerGameData = getPlayerData(player);
        player.setPlayerGameData(playerGameData);
        return player;
    }
}
