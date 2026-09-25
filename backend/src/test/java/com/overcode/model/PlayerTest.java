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

//    @Test
//    void actualizarMetricasActualizaElEstadoSiNoEsNulo() {
//        Player player = new Player(1L, "leo");
//        WeeklyMetrics metrics = new WeeklyMetrics();
//        metrics.setGoals(2);
//        player.actualizarMetricas(metrics);
//        assertEquals(2, player.getMetrics().getGoals());
//    }
//
//    @Test
//    void actualizarMetricasIgnoraSiEsNulo() {
//        Player player = new Player(1L, "leo");
//        player.actualizarMetricas(null);
//        org.junit.jupiter.api.Assertions.assertNull(player.getMetrics());
//    }

}
