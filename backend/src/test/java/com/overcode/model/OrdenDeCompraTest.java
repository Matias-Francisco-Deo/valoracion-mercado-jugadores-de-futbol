package com.overcode.model;

import com.overcode.model.player.Player;
import com.overcode.model.token.Token;
import com.overcode.model.user.User;
import com.overcode.model.venta.OrdenDeCompra;
import com.overcode.model.venta.orden.OrderStatus;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class OrdenDeCompraTest {

    private final User SELLER = new User(10L, "seller", "seller@gmail.com", "pass");

    private OrdenDeCompra openOrder(Double price) {

        Player player = new Player();
        player.setId(20L);

        Token token = new Token(100L, null, player);
        return new OrdenDeCompra(1L, SELLER, player, token, price, OrderStatus.OPEN, Instant.now());
    }

    @Test
    void isOpenReflectsStatus() {
        OrdenDeCompra order = openOrder(25d);
        assertTrue(order.isOpen());
        order.markCompleted();
        assertFalse(order.isOpen());
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    void belongsToMatchesSeller() {
        OrdenDeCompra order = openOrder(25d);
        assertTrue(order.belongsTo(10L));
        assertFalse(order.belongsTo(99L));
    }

    @Test
    @Disabled("Esta feature todavía no está en uso")
    void cancelOrderMarksCancelled() {
        OrdenDeCompra order = openOrder(25d);
        order.cancel(SELLER.getId());
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertFalse(order.isOpen());
    }

    @Test
    @Disabled("Esta feature todavía no está en uso")
    void cannotCompleteACancelledOrder() {
        OrdenDeCompra order = openOrder(25d);
        order.cancel(SELLER.getId());
        assertThrows(IllegalStateException.class, order::markCompleted);
    }

    @Test
    @Disabled("Esta feature todavía no está en uso")
    void cannotCancelACompletedOrder() {
        OrdenDeCompra order = openOrder(25d);
        order.markCompleted();
        Long sellerId = SELLER.getId();
        assertThrows(IllegalStateException.class, () -> order.cancel(sellerId));
    }
}
