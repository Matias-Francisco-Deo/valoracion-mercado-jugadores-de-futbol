package com.overcode.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerGameDataTest {

    @Test
    void debeCrearInstanciaCorrectamenteConTodosLosCampos() {
        Player player = new Player();
        player.setId(1L);

        PlayerGameData data = new PlayerGameData(10L, player, 5, 3, 10, 2, 4, 6, 8.5, "Forward");

        assertEquals(10L, data.getId());
        assertEquals(player, data.getPlayer());
        assertEquals(5, data.getGoals());
        assertEquals(3, data.getAssists());
        assertEquals(10, data.getShotsOnTarget());
        assertEquals(2, data.getKeyPasses());
        assertEquals(4, data.getTackles());
        assertEquals(6, data.getSuccessfulDribbles());
        assertEquals(8.5, data.getRating());
        assertEquals("Forward", data.getPosition());
    }

    @Test
    void debeCrearInstanciaCorrectamenteSinId() {
        Player player = new Player();
        PlayerGameData data = new PlayerGameData(player, 5, 3, 10, 2, 4, 6, 8.5, "Midfielder");
        
        assertNull(data.getId());
        assertEquals(player, data.getPlayer());
        assertEquals("Midfielder", data.getPosition());
    }

    @Test
    void debeCrearInstanciaCorrectamenteSinIdNiPlayer() {
        PlayerGameData data = new PlayerGameData(5, 3, 10, 2, 4, 6, 8.5, "Defender");
        
        assertNull(data.getId());
        assertNull(data.getPlayer());
        assertEquals("Defender", data.getPosition());
    }
}
