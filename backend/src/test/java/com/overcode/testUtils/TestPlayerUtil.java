package com.overcode.testUtils;

import com.overcode.model.Player;

public class TestPlayerUtil {
    static public Player getJugadorConRating(String name, Double rating) {
        return new Player(name, "Club", "Liga1",0, 10, 5, 20, 3, 2, 0, rating, 5);
    }

    static public Player getJugadorConClub(String name, String club) {
        return new Player(name, club, "Liga1",0, 10, 5, 20, 3, 2, 0, 5.0, 5);
    }

    static public Player getJugadorConLiga(String name, String liga) {
        return new Player(name, "Club", liga,0, 10, 5, 20, 3, 2, 0, 5.0, 5);
    }

    static public Player getJugadorConNombre(String name) {
        return new Player(name, "Club", "Liga1",0, 10, 5, 20, 3, 2, 0, 2.0, 5);
    }
}
