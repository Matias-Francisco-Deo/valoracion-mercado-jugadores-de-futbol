package com.overcode.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerTest {

    @Test
    void alCrearAlJugadorComienzaConPrecio1() {
        Player player = getJugadorConName("leo");
        assertEquals(1, player.getCurrentPrice());
    }

    @Test
    void alCrearAlJugadorComienzaCon100Tokens() {
        Player player = getJugadorConName("leo");
        assertEquals(100, player.getTokens().size());
    }

    @Test
    void alAsignarPrecioActualMenorQue1Es1() {
        Player player = getJugadorConName("leo");
        player.setCurrentPrice(-5);
        assertEquals(1, player.getCurrentPrice());
    }

    private Player getJugadorConName(String name) {
        return new Player(name, "Club", 10, 5, 20, 3, 2, 2.0, 5);
    }


}
