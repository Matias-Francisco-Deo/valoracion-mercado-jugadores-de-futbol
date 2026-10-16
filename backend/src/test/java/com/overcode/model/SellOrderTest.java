package com.overcode.model;

import com.overcode.model.orden.OrderStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class SellOrderTest {

    private SellOrder openOrder(int price) {
        return new SellOrder(1L, 10L, 20L, 100L, price, OrderStatus.OPEN, Instant.now());
    }

    @Test
    void isOpenReflectsStatus() {
        SellOrder order = openOrder(25);
        assertTrue(order.isOpen());
        order.markCompleted();
        assertFalse(order.isOpen());
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    void belongsToMatchesSeller() {
        SellOrder order = openOrder(25);
        assertTrue(order.belongsTo(10L));
        assertFalse(order.belongsTo(99L));
    }

    @Test
    void cancelOrderMarksCancelled() {
        SellOrder order = openOrder(25);
        order.cancel();
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertFalse(order.isOpen());
    }

    @Test
    void cannotCompleteACancelledOrder() {
        SellOrder order = openOrder(25);
        order.cancel();
        assertThrows(IllegalStateException.class, order::markCompleted);
    }

    @Test
    void cannotCancelACompletedOrder() {
        SellOrder order = openOrder(25);
        order.markCompleted();
        assertThrows(IllegalStateException.class, order::cancel);
    }
}
