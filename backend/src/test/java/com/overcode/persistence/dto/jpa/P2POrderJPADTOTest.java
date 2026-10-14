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
        P2POrder modelo = P2POrder.builder()
                .id(id)
                .userId(1L)
                .playerId(10L)
                .type(OrderType.SELL)
                .quantity(5)
                .pricePerToken(new BigDecimal("25.00"))
                .status(OrderStatus.OPEN)
                .createdAt(createdAt)
                .version(3L)
                .build();

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
        P2POrder original = P2POrder.builder()
                .id(id)
                .userId(1L)
                .playerId(10L)
                .type(OrderType.BUY)
                .quantity(7)
                .pricePerToken(new BigDecimal("12.50"))
                .status(OrderStatus.COMPLETED)
                .createdAt(createdAt)
                .version(2L)
                .build();
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
        P2POrder nuevaOrden = P2POrder.builder()
                .id(UUID.randomUUID())
                .userId(1L)
                .playerId(10L)
                .type(OrderType.SELL)
                .quantity(5)
                .pricePerToken(new BigDecimal("25.00"))
                .status(OrderStatus.OPEN)
                .createdAt(Instant.now())
                .version(null)
                .build();

        P2POrderJPADTO dto = P2POrderJPADTO.desdeModelo(nuevaOrden);

        assertNull(dto.getVersion());
    }
}
