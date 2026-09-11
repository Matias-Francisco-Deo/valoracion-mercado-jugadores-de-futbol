package com.overcode.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerTest {

    @Test
    void alCrearAlJugadorComienzaConPrecio1() {
        Player player = new Player(1L, "leo");
        assertEquals(1, player.getCurrentPrice());
    }

    @Test
    void alCrearAlJugadorComienzaCon100Tokens() {
        Player player = new Player(1L, "leo");
        assertEquals(100, player.getTokens().size());
    }

    @Test
    void alAsignarPrecioActualMenorQue1Es1() {
        Player player = new Player(1L, "leo");
        player.setCurrentPrice(-5);
        assertEquals(1, player.getCurrentPrice());
    }

}
