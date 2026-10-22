package com.overcode.model;

import com.overcode.model.exception.InvalidOrderQuantityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BuyOrderTest {
    private User user;
    private Player player;

    @BeforeEach
    void setUp() {
        user = new User(1L, "testuser", "test@email.com", "password", 1000, new ArrayList<>());

        player = new Player(10L, 100L, "Test Player", null, null);
    }

    @Test
    void createsBuyOrderSuccessfullyWithValidQuantity() {
        BuyOrder order = new BuyOrder(user, player, 5);

        assertEquals(1L, order.getOwner().getId());
        assertEquals(10L, order.getPlayer().getId());
        assertEquals(5, order.getQuantity());
        assertNotNull(order.getCreatedAt());
    }

    @Test
    void throwsInvalidOrderQuantityExceptionWhenQuantityIsZero() {
        InvalidOrderQuantityException ex = assertThrows(
                InvalidOrderQuantityException.class,
                () -> new BuyOrder(user, player, 0)
        );
        assertEquals("Quantity must be greater than zero.", ex.getMessage());
    }

    @Test
    void throwsInvalidOrderQuantityExceptionWhenQuantityIsNegative() {
        InvalidOrderQuantityException ex = assertThrows(
                InvalidOrderQuantityException.class,
                () -> new BuyOrder(user, player, -3)
        );
        assertEquals("Quantity must be greater than zero.", ex.getMessage());
    }

    @Test
    void throwsInvalidOrderQuantityExceptionWhenQuantityIsNull() {
        InvalidOrderQuantityException ex = assertThrows(
                InvalidOrderQuantityException.class,
                () -> new BuyOrder(user, player, null)
        );
        assertEquals("Quantity must be greater than zero.", ex.getMessage());
    }

    @Test
    void defaultConstructorInstantiatesSuccessfully() {
        BuyOrder order = new BuyOrder();
        assertNull(order.getQuantity());
    }
}
