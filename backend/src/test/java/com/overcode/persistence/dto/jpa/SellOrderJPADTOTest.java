package com.overcode.persistence.dto.jpa;

import com.overcode.model.User;
import com.overcode.model.orden.OrderStatus;
import com.overcode.model.SellOrder;
import com.overcode.model.player.Player;
import com.overcode.model.token.Token;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SellOrderJPADTOTest {

    @Test
    void desdeModeloMapeaTodosLosCamposCorrectamente() {
        User user = new User(10L,"user","email@gmail","pass",0,new ArrayList<>());
        Player player = new Player(20L);
        Token token = new Token(100L,user,player);

        Instant now = Instant.now();
        SellOrder modelo = new SellOrder(1L, user, player, token, 15, OrderStatus.OPEN, now);

        SellOrderJPADTO dto = SellOrderJPADTO.desdeModelo(modelo);

        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getOwner().getId());
        assertEquals(20L, dto.getPlayer().getId());
        assertEquals(100L, dto.getToken().getId());
        assertEquals(15, dto.getPrice());
        assertEquals(OrderStatus.OPEN, dto.getStatus());
        assertEquals(now, dto.getCreatedAt());
    }

    @Test
    void aModeloMapeaTodosLosCamposCorrectamente() {
        UserJPADTO user = new UserJPADTO(11L,"user2","email2@gmail","pass",0,new ArrayList<>());
        PlayerJPADTO player = new PlayerJPADTO(21L,"player",30,new ArrayList<>());
        TokenJPADTO token = new TokenJPADTO(101L,user,player);

        Instant now = Instant.now();
        SellOrderJPADTO dto = new SellOrderJPADTO(2L, user, player, token, 30, OrderStatus.COMPLETED, now);

        SellOrder modelo = dto.aModelo();

        assertEquals(2L, modelo.getId());
        assertEquals(11L, modelo.getOwner().getId());
        assertEquals(21L, modelo.getPlayer().getId());
        assertEquals(101L, modelo.getToken().getId());
        assertEquals(30, modelo.getPrice());
        assertEquals(OrderStatus.COMPLETED, modelo.getStatus());
        assertEquals(now, modelo.getCreatedAt());
    }
}
