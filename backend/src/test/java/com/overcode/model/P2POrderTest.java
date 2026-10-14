package com.overcode.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class P2POrderTest {

    private P2POrder openOrder(int quantity, String price) {
        return P2POrder.builder()
                .id(UUID.randomUUID())
                .userId(1L)
                .playerId(10L)
                .type(OrderType.SELL)
                .quantity(quantity)
                .pricePerToken(new BigDecimal(price))
                .status(OrderStatus.OPEN)
                .createdAt(Instant.now())
                .version(null)
                .build();
    }

    @Test
    void isOpenReflectsStatus() {
        P2POrder order = openOrder(5, "2.00");
        assertTrue(order.isOpen());
        order.markCompleted();
        assertFalse(order.isOpen());
    }

    @Test
    void belongsToMatchesOwner() {
        P2POrder order = openOrder(5, "2.00");
        assertTrue(order.belongsTo(1L));
        assertFalse(order.belongsTo(2L));
    }

    @Test
    void totalValueIsPriceTimesQuantity() {
        P2POrder order = openOrder(5, "2.50");
        assertEquals(0, new BigDecimal("12.50").compareTo(order.totalValue()));
    }

    @Test
    void reduceQuantityPartialKeepsOpen() {
        P2POrder order = openOrder(10, "1.00");
        order.reduceQuantity(4);
        assertEquals(6, order.getQuantity());
        assertTrue(order.isOpen());
    }

    @Test
    void reduceQuantityToZeroCompletes() {
        P2POrder order = openOrder(10, "1.00");
        order.reduceQuantity(10);
        assertEquals(0, order.getQuantity());
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    void reduceQuantityRejectsOverTake() {
        P2POrder order = openOrder(3, "1.00");
        assertThrows(IllegalArgumentException.class, () -> order.reduceQuantity(4));
    }

    @Test
    void reduceQuantityRejectsNonPositive() {
        P2POrder order = openOrder(3, "1.00");
        assertThrows(IllegalArgumentException.class, () -> order.reduceQuantity(0));
    }

    @Test
    void cannotCompleteACancelledOrder() {
        P2POrder order = openOrder(3, "1.00");
        order.cancel();
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertThrows(IllegalStateException.class, order::markCompleted);
    }
}
