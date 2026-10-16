package com.overcode.model;

import com.overcode.model.exception.InvalidOrderQuantityException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BuyOrderTest {

    @Test
    void createsBuyOrderSuccessfullyWithValidQuantity() {
        BuyOrder order = new BuyOrder(1L, 10L, 5);

        assertEquals(1L, order.getUserId());
        assertEquals(10L, order.getPlayerId());
        assertEquals(5, order.getQuantity());
        assertNotNull(order.getCreatedAt());
    }

    @Test
    void throwsInvalidOrderQuantityExceptionWhenQuantityIsZero() {
        InvalidOrderQuantityException ex = assertThrows(
                InvalidOrderQuantityException.class,
                () -> new BuyOrder(1L, 10L, 0)
        );
        assertEquals("Quantity must be greater than zero.", ex.getMessage());
    }

    @Test
    void throwsInvalidOrderQuantityExceptionWhenQuantityIsNegative() {
        InvalidOrderQuantityException ex = assertThrows(
                InvalidOrderQuantityException.class,
                () -> new BuyOrder(1L, 10L, -3)
        );
        assertEquals("Quantity must be greater than zero.", ex.getMessage());
    }

    @Test
    void throwsInvalidOrderQuantityExceptionWhenQuantityIsNull() {
        InvalidOrderQuantityException ex = assertThrows(
                InvalidOrderQuantityException.class,
                () -> new BuyOrder(1L, 10L, null)
        );
        assertEquals("Quantity must be greater than zero.", ex.getMessage());
    }

    @Test
    void defaultConstructorInstantiatesSuccessfully() {
        BuyOrder order = new BuyOrder();
        assertNull(order.getQuantity());
    }
}
