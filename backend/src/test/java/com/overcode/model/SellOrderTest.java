package com.overcode.model;

import com.overcode.model.orden.OrderStatus;
import com.overcode.model.player.Player;
import com.overcode.model.token.Token;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class SellOrderTest {

    private SellOrder openOrder(Double price) {
        User seller = new User("seller", "seller@gmail.com", "pass");
        seller.setId(10L);

        Player player = new Player();
        player.setId(20L);

        Token token = new Token(100L, null, player);
        return new SellOrder(1L, seller, player, token, price, OrderStatus.OPEN, Instant.now());
    }

    @Test
    void isOpenReflectsStatus() {
        SellOrder order = openOrder(25d);
        assertTrue(order.isOpen());
        order.markCompleted();
        assertFalse(order.isOpen());
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    void belongsToMatchesSeller() {
        SellOrder order = openOrder(25d);
        assertTrue(order.belongsTo(10L));
        assertFalse(order.belongsTo(99L));
    }

    @Test
    void cancelOrderMarksCancelled() {
        SellOrder order = openOrder(25d);
        order.cancel();
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertFalse(order.isOpen());
    }

    @Test
    void cannotCompleteACancelledOrder() {
        SellOrder order = openOrder(25d);
        order.cancel();
        assertThrows(IllegalStateException.class, order::markCompleted);
    }

    @Test
    void cannotCancelACompletedOrder() {
        SellOrder order = openOrder(25d);
        order.markCompleted();
        assertThrows(IllegalStateException.class, order::cancel);
    }
}
