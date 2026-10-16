package com.overcode.controller.dto.player;

import com.overcode.model.PlayerGameData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerGameDataResponseDTOTest {

    @Test
    void desdeModeloDebeMapearCorrectamenteHaciaResponseDTO() {
        PlayerGameData data = new PlayerGameData(10L, null, 5, 3, 10, 2, 4, 6, 8.5, "Forward");
        
        PlayerGameDataResponseDTO dto = PlayerGameDataResponseDTO.desdeModelo(data, 1L);
        
        assertNotNull(dto);
        assertEquals(10L, dto.id());
        assertEquals(1L, dto.playerId());
        assertEquals(5, dto.goals());
        assertEquals(10, dto.shotsOnTarget());
        assertEquals(4, dto.tackles());
        assertEquals(8.5, dto.rating());
        assertEquals("Forward", dto.position());
    }

    @Test
    void desdeModeloDebeRetornarNullSiElModeloEsNull() {
        assertNull(PlayerGameDataResponseDTO.desdeModelo(null, 1L));
    }
}
