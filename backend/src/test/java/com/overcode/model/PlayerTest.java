package com.overcode.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerTest {

    @Test
    void alCrearAlJugadorComienzaConPrecio1y100TokensEmitidos() {
        Player player = new Player(1L, "leo");
        assertEquals(1, player.getCurrentPrice());
        assertEquals(100, player.getTotalTokensIssued());
    }

    @Test
    void alAsignarPrecioActualMenorQue1Es1() {
        Player player = new Player(1L, "leo", 10, 100);
        player.setCurrentPrice(-5);
        assertEquals(1, player.getCurrentPrice());
    }

    @Test
    void alAsignarTotalTokensEmitidosNegativoEs0() {
        Player player = new Player(1L, "leo", 10, 100);
        player.setTotalTokensIssued(-20);
        assertEquals(0, player.getTotalTokensIssued());
    }
}
