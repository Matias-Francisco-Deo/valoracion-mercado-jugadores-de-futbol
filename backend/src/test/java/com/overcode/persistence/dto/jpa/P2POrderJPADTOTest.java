package com.overcode.persistence.dto.jpa;

import com.overcode.model.OrderStatus;
import com.overcode.model.OrderType;
import com.overcode.model.P2POrder;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class P2POrderJPADTOTest {

    @Test
    void desdeModeloMapeaTodosLosCamposCorrectamente() {
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.now();
        P2POrder modelo = new P2POrder(id, 1L, 10L, OrderType.SELL, 5,
                new BigDecimal("25.00"), OrderStatus.OPEN, createdAt, 3L);

        P2POrderJPADTO dto = P2POrderJPADTO.desdeModelo(modelo);

        assertEquals(id, dto.getId());
        assertEquals(1L, dto.getUserId());
        assertEquals(10L, dto.getPlayerId());
        assertEquals(OrderType.SELL, dto.getType());
        assertEquals(5, dto.getQuantity());
        assertEquals(0, new BigDecimal("25.00").compareTo(dto.getPricePerToken()));
        assertEquals(OrderStatus.OPEN, dto.getStatus());
        assertEquals(createdAt, dto.getCreatedAt());
        assertEquals(3L, dto.getVersion());
    }

    @Test
    void aModeloMapeaTodosLosCamposCorrectamente() {
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.now();
        P2POrder original = new P2POrder(id, 1L, 10L, OrderType.BUY, 7,
                new BigDecimal("12.50"), OrderStatus.COMPLETED, createdAt, 2L);
        P2POrderJPADTO dto = P2POrderJPADTO.desdeModelo(original);

        P2POrder roundTripped = dto.aModelo();

        assertEquals(id, roundTripped.getId());
        assertEquals(1L, roundTripped.getUserId());
        assertEquals(10L, roundTripped.getPlayerId());
        assertEquals(OrderType.BUY, roundTripped.getType());
        assertEquals(7, roundTripped.getQuantity());
        assertEquals(0, new BigDecimal("12.50").compareTo(roundTripped.getPricePerToken()));
        assertEquals(OrderStatus.COMPLETED, roundTripped.getStatus());
        assertEquals(createdAt, roundTripped.getCreatedAt());
        assertEquals(2L, roundTripped.getVersion());
    }

    @Test
    void desdeModeloPreservaVersionNullParaOrdenesNuevas() {
        P2POrder nuevaOrden = new P2POrder(UUID.randomUUID(), 1L, 10L, OrderType.SELL, 5,
                new BigDecimal("25.00"), OrderStatus.OPEN, Instant.now(), null);

        P2POrderJPADTO dto = P2POrderJPADTO.desdeModelo(nuevaOrden);

        assertNull(dto.getVersion());
    }
}
