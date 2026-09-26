package com.overcode.model;

import org.junit.jupiter.api.Test;

import static com.overcode.testUtils.TestPlayerUtil.getJugadorConNombre;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerTest {

    @Test
    void alCrearAlJugadorComienzaConPrecio1() {
        Player player = getJugadorConNombre("leo");
        assertEquals(1, player.getCurrentPrice());
    }

    @Test
    void alCrearAlJugadorComienzaCon100Tokens() {
        Player player = getJugadorConNombre("leo");
        assertEquals(100, player.getTokens().size());
    }

    @Test
    void alAsignarPrecioActualMenorQue1Es1() {
        Player player = getJugadorConNombre("leo");
        player.setCurrentPrice(-5);
        assertEquals(1, player.getCurrentPrice());
    }



}
