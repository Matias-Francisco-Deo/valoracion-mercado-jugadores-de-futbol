package com.overcode.persistence.dto.jpa;

import com.overcode.model.orden.OrderStatus;
import com.overcode.model.SellOrder;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class SellOrderJPADTOTest {

    @Test
    void desdeModeloMapeaTodosLosCamposCorrectamente() {
        Instant now = Instant.now();
        SellOrder modelo = new SellOrder(1L, 10L, 20L, 100L, 15, OrderStatus.OPEN, now);

        SellOrderJPADTO dto = SellOrderJPADTO.desdeModelo(modelo);

        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getUserId());
        assertEquals(20L, dto.getPlayerId());
        assertEquals(100L, dto.getTokenId());
        assertEquals(15, dto.getPrice());
        assertEquals(OrderStatus.OPEN, dto.getStatus());
        assertEquals(now, dto.getCreatedAt());
    }

    @Test
    void aModeloMapeaTodosLosCamposCorrectamente() {
        Instant now = Instant.now();
        SellOrderJPADTO dto = new SellOrderJPADTO(2L, 11L, 21L, 101L, 30, OrderStatus.COMPLETED, now);

        SellOrder modelo = dto.aModelo();

        assertEquals(2L, modelo.getId());
        assertEquals(11L, modelo.getUserId());
        assertEquals(21L, modelo.getPlayerId());
        assertEquals(101L, modelo.getTokenId());
        assertEquals(30, modelo.getPrice());
        assertEquals(OrderStatus.COMPLETED, modelo.getStatus());
        assertEquals(now, modelo.getCreatedAt());
    }
}
