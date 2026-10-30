package com.overcode.model;

import com.overcode.model.exception.OperacionInvalidaOrdenException;
import com.overcode.model.market.orden.OrdenDeVenta;
import com.overcode.model.market.orden.OrderStatus;
import com.overcode.model.player.Player;
import com.overcode.model.token.Token;
import com.overcode.model.user.User;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class OrdenDeVentaTest {

    private final User SELLER = new User(10L, "seller", "seller@gmail.com", "pass");

    private OrdenDeVenta openOrder(Double price) {

        Player player = new Player();
        player.setId(20L);

        Token token = new Token(100L, null, player);
        return new OrdenDeVenta(1L, SELLER, player, token, price, OrderStatus.OPEN, Instant.now());
    }

    @Test
    void isOpenReflectsStatus() {
        OrdenDeVenta order = openOrder(25d);
        assertTrue(order.isOpen());
        order.markCompleted();
        assertFalse(order.isOpen());
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    void belongsToMatchesSeller() {
        OrdenDeVenta order = openOrder(25d);
        assertTrue(order.belongsTo(new User(10L)));
        assertFalse(order.belongsTo(new User(99L)));
    }

    @Test
    @Disabled("Esta feature todavía no está en uso")
    void cancelOrderMarksCancelled() {
        OrdenDeVenta order = openOrder(25d);
        order.cancel(SELLER);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertFalse(order.isOpen());
    }

    @Test
    @Disabled("Esta feature todavía no está en uso")
    void cannotCompleteACancelledOrder() {
        OrdenDeVenta order = openOrder(25d);
        order.cancel(SELLER);
        assertThrows(OperacionInvalidaOrdenException.class, order::markCompleted);
    }

    @Test
    @Disabled("Esta feature todavía no está en uso")
    void cannotCancelACompletedOrder() {
        OrdenDeVenta order = openOrder(25d);
        order.markCompleted();
        assertThrows(OperacionInvalidaOrdenException.class, () -> order.cancel(SELLER));
    }
}
