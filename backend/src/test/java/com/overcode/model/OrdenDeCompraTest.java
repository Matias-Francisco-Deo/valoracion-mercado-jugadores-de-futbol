package com.overcode.model;

import com.overcode.model.exception.InvalidOrderQuantityException;
import com.overcode.model.market.orden.OrdenDeCompra;
import com.overcode.model.player.Player;
import com.overcode.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class OrdenDeCompraTest {
    private User user;
    private Player player;

    @BeforeEach
    void setUp() {
        user = new User(1L, "testuser", "test@email.com", "password", 1000d, new ArrayList<>());

        player = new Player(10L, 100L, "Test Player", null, null);
    }

    @Test
    void createsBuyOrderSuccessfullyWithValidQuantity() {
        OrdenDeCompra order = new OrdenDeCompra(user, player, 5);

        assertEquals(1L, order.getOwner().getId());
        assertEquals(10L, order.getPlayer().getId());
        assertEquals(5, order.getQuantity());
        assertNotNull(order.getCreatedAt());
    }

    @Test
    void throwsInvalidOrderQuantityExceptionWhenQuantityIsZero() {
        assertThrows(
                InvalidOrderQuantityException.class,
                () -> new OrdenDeCompra(user, player, 0)
        );
    }

    @Test
    void throwsInvalidOrderQuantityExceptionWhenQuantityIsNegative() {
        assertThrows(
                InvalidOrderQuantityException.class,
                () -> new OrdenDeCompra(user, player, -3)
        );
    }

    @Test
    void throwsInvalidOrderQuantityExceptionWhenQuantityIsNull() {
        assertThrows(
                InvalidOrderQuantityException.class,
                () -> new OrdenDeCompra(user, player, null)
        );
    }

    @Test
    void defaultConstructorInstantiatesSuccessfully() {
        OrdenDeCompra order = new OrdenDeCompra();
        assertNull(order.getQuantity());
    }
}
