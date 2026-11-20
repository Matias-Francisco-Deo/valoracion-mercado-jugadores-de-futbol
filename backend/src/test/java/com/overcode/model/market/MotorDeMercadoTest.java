package com.overcode.model.market;

import com.overcode.model.exception.TokensInsuficientesParaComprarException;
import com.overcode.model.market.orden.OrdenDeVenta;
import com.overcode.model.market.transaction.TransactionType;
import com.overcode.model.player.Player;
import com.overcode.model.token.Token;
import com.overcode.model.user.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MotorDeMercadoTest {

    @Test
    void publicarJugadorStillCreatesOneOpenOrderPerInitialToken() {
        User superuser = new User(2L, "admin", "admin@test.com", "pass");
        Player player = new Player(10L);
        player.setCurrentPrice(20.0);
        Token first = new Token(101L, null, player);
        Token second = new Token(102L, null, player);
        player.setTokens(new ArrayList<>(List.of(first, second)));

        var orders = new MotorDeMercado().publicarJugador(player, superuser);

        assertEquals(2, orders.size());
        assertTrue(superuser.getTokens().isEmpty());
        assertTrue(orders.stream().allMatch(o -> o.isOpen() && o.getOwner() == superuser && o.getPrice() == 20.0));
        assertEquals(List.of(first, second), orders.stream().map(OrdenDeVenta::getToken).toList());
        assertNull(first.getOwner());
        assertNull(second.getOwner());
    }

    @Test
    void comprarOrdenesDoesNotFillMoreThanRequested() {
        User buyer = new User(1L, "buyer", "buyer@test.com", "pass");
        buyer.setCreditBalance(100.0);
        User seller = new User(2L, "seller", "seller@test.com", "pass");
        seller.setCreditBalance(10.0);
        Player player = new Player(10L);
        Token first = new Token(101L, seller, player);
        Token second = new Token(102L, seller, player);
        OrdenDeVenta selected = new OrdenDeVenta(seller, player, first, 20.0);
        OrdenDeVenta untouched = new OrdenDeVenta(seller, player, second, 20.0);

        TransaccionCompra result = new MotorDeMercado().comprarOrdenes(
                List.of(selected, untouched), 1, buyer);

        assertEquals(List.of(selected), result.ordenesCompra());
        assertEquals(80.0, buyer.getCreditBalance());
        assertEquals(30.0, seller.getCreditBalance());
        assertEquals(List.of(first), buyer.getTokens());
        assertFalse(selected.isOpen());
        assertTrue(untouched.isOpen());
        assertSame(seller, second.getOwner());
        assertEquals(2, result.transaccionesFinancieras().size());
        assertTrue(result.transaccionesFinancieras().stream().allMatch(t -> t.getQuantity() == 1));
    }

    @Test
    void comprarOrdenesThrowsExceptionWhenInsufficientOrders() {
        MotorDeMercado motor = new MotorDeMercado();
        User buyer = new User(1L, "buyer", "buyer@test.com", "pass");
        buyer.setCreditBalance(100.0);

        List<OrdenDeVenta> orders = List.of();

        assertThrows(TokensInsuficientesParaComprarException.class, () ->
                motor.comprarOrdenes(orders, 1, buyer)
        );
    }

    @Test
    void comprarOrdenesFillsAllProvidedOrdersAndCreatesTransactions() {
        MotorDeMercado motor = new MotorDeMercado();

        User buyer = new User(1L, "buyer", "buyer@test.com", "pass");
        buyer.setCreditBalance(100.0);

        User seller = new User(2L, "seller", "seller@test.com", "pass");
        seller.setCreditBalance(10.0);

        Player player = new Player();
        player.setId(10L);
        player.setName("Lionel");

        Token token1 = new Token();
        token1.setId(101L);
        token1.setPlayer(player);
        token1.setOwner(seller);

        Token token2 = new Token();
        token2.setId(102L);
        token2.setPlayer(player);
        token2.setOwner(seller);

        OrdenDeVenta order1 = new OrdenDeVenta(seller, player, token1, 20.0);
        order1.setId(1001L);
        OrdenDeVenta order2 = new OrdenDeVenta(seller, player, token2, 20.0);
        order2.setId(1002L);

        List<OrdenDeVenta> orders = new ArrayList<>(List.of(order1, order2));

        TransaccionCompra result = motor.comprarOrdenes(orders, 2, buyer);

        assertEquals(2, result.ordenesCompra().size());
        assertEquals(60.0, buyer.getCreditBalance());
        assertEquals(50.0, seller.getCreditBalance());
        assertEquals(2, buyer.getTokens().size());

        // Check transactions
        assertEquals(2, result.transaccionesFinancieras().size());
        assertTrue(result.transaccionesFinancieras().stream()
                .anyMatch(t -> t.getType() == TransactionType.BUY && t.getQuantity() == 2 && t.getOwner().equals(buyer)));
        assertTrue(result.transaccionesFinancieras().stream()
                .anyMatch(t -> t.getType() == TransactionType.SELL && t.getQuantity() == 2 && t.getOwner().equals(seller)));
    }
}
