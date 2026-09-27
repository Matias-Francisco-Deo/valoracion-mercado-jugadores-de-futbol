package com.overcode.testUtils;

import com.overcode.model.Player;

public class TestPlayerUtil {
    public static Player getJugadorConRating(String name, Double rating) {
        return new Player(name, "Club", "Liga1",0, 10, 5, 20, 3, 2, 0, rating, 5);
    }

    public static Player getJugadorConClub(String name, String club) {
        return new Player(name, club, "Liga1",0, 10, 5, 20, 3, 2, 0, 5.0, 5);
    }

    public static Player getJugadorConLiga(String name, String liga) {
        return new Player(name, "Club", liga,0, 10, 5, 20, 3, 2, 0, 5.0, 5);
    }

    public static Player getJugadorConLigaYClub(String name, String liga, String clubName) {
        Player jugador = getJugadorConLiga(name, liga);
        jugador.setClubName(clubName);
        return jugador;
    }


    public static Player getJugadorConNombre(String name) {
        return new Player(name, "Club", "Liga1",0, 10, 5, 20, 3, 2, 0, 2.0, 5);
    }
}
