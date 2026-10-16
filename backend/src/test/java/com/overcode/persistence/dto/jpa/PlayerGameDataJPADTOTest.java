package com.overcode.persistence.dto.jpa;

import com.overcode.model.Player;
import com.overcode.model.PlayerGameData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerGameDataJPADTOTest {

    @Test
    void desdeModeloDebeMapearTodosLosCamposCorrectamente() {
        Player player = new Player();
        player.setId(1L);
        player.setName("Messi");

        PlayerGameData data = new PlayerGameData(10L, player, 5, 3, 10, 2, 4, 6, 8.5, "Forward");
        
        PlayerGameDataJPADTO dto = PlayerGameDataJPADTO.desdeModelo(data);
        
        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals("Forward", dto.getPosition());
        assertEquals(5, dto.getGoals());
        assertEquals(3, dto.getAssists());
        assertEquals(10, dto.getShotsOnTarget());
        assertEquals(2, dto.getKeyPasses());
        assertEquals(4, dto.getTackles());
        assertEquals(6, dto.getSuccessfulDribbles());
        assertEquals(8.5, dto.getRating());
        
        assertNotNull(dto.getPlayer());
        assertEquals(1L, dto.getPlayer().getId());
        assertEquals("Messi", dto.getPlayer().getName());
    }

    @Test
    void desdeModeloDebeRetornarNullSiElModeloEsNull() {
        assertNull(PlayerGameDataJPADTO.desdeModelo(null));
        assertNull(PlayerGameDataJPADTO.desdeModelo(null, null));
    }

    @Test
    void desdeModeloDebeMapearConPlayerJPAExistente() {
        PlayerGameData data = new PlayerGameData(10L, null, 5, 3, 10, 2, 4, 6, 8.5, "Forward");
        PlayerJPADTO playerDto = new PlayerJPADTO();
        playerDto.setId(2L);
        
        PlayerGameDataJPADTO dto = PlayerGameDataJPADTO.desdeModelo(data, playerDto);
        
        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals("Forward", dto.getPosition());
        assertEquals(playerDto, dto.getPlayer());
    }

    @Test
    void aModeloDebeMapearCorrectamenteAlModeloDeDominio() {
        PlayerGameDataJPADTO dto = new PlayerGameDataJPADTO(10L, null, 5, 3, 10, 4, 2, 8.5, 6, "Midfielder");
        Player player = new Player();
        player.setId(1L);
        
        PlayerGameData data = dto.aModelo(player);
        
        assertNotNull(data);
        assertEquals(10L, data.getId());
        assertEquals("Midfielder", data.getPosition());
        assertEquals(5, data.getGoals());
        assertEquals(player, data.getPlayer());
    }
}
