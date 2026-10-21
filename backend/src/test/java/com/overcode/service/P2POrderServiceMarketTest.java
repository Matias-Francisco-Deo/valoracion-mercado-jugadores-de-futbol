package com.overcode.service;

import com.overcode.model.BuyOrder;
import com.overcode.model.SellOrder;
import com.overcode.model.User;
import com.overcode.model.exception.InsufficientFundsException;
import com.overcode.model.orden.OrderStatus;
import com.overcode.model.player.Player;
import com.overcode.model.token.Token;
import com.overcode.model.transaction.Transaction;
import com.overcode.model.transaction.TransactionType;
import com.overcode.persistence.repository.interfaces.*;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.exception.InsufficientLiquidityException;
import com.overcode.service.impl.P2POrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class P2POrderServiceMarketTest {

    private final SellOrderRepository sellOrderRepository = mock(SellOrderRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final PlayerRepository playerRepository = mock(PlayerRepository.class);
    private final TokenRepository tokenRepository = mock(TokenRepository.class);
    private final TransactionRepository transactionRepository = mock(TransactionRepository.class);

    private final P2POrderServiceImpl service =
            new P2POrderServiceImpl(sellOrderRepository, userRepository, playerRepository, tokenRepository, transactionRepository);

    private static final Long PLAYER = 10L;

    private Player playerWithPrice(int price) {
        Player player = new Player();
        player.setId(PLAYER);
        player.setName("Lionel Messi");
        player.setCurrentPrice(price);
        return player;
    }

    private SellOrder sellOrder(Long id, Long userId, Long tokenId, int price) {
        return new SellOrder(id, userId, PLAYER, tokenId, price, OrderStatus.OPEN, Instant.now());
    }

    private User userWithCredits(Long userId, int credits) {
        User user = new User("user-" + userId, "user" + userId + "@gmail.com", "pass");
        user.setId(userId);
        if (credits > 0) {
            user.creditCredits(credits);
        }
        return user;
    }

    @Test
    void executeMarketBuyEjecutaCompraDeMultiplesOrdenesYGuardaTransacciones() {
        Player player = playerWithPrice(10);
        User buyer = userWithCredits(1L, 100);
        User sellerA = userWithCredits(2L, 0);
        User sellerB = userWithCredits(3L, 0);

        SellOrder askA = sellOrder(101L, 2L, 201L, 1);
        SellOrder askB = sellOrder(102L, 3L, 202L, 2);

        when(userRepository.recuperar(1L)).thenReturn(Optional.of(buyer));
        when(userRepository.recuperar(2L)).thenReturn(Optional.of(sellerA));
        when(userRepository.recuperar(3L)).thenReturn(Optional.of(sellerB));
        when(playerRepository.recuperar(PLAYER)).thenReturn(Optional.of(player));
        when(sellOrderRepository.findOpenSellOrders(PLAYER)).thenReturn(List.of(askA, askB));
        when(tokenRepository.findById(201L)).thenReturn(Optional.of(new Token(201L, null, player)));
        when(tokenRepository.findById(202L)).thenReturn(Optional.of(new Token(202L, null, player)));

        service.executeMarketBuy(new BuyOrder(1L, PLAYER, 2));

        // 1 + 2 = 3 debitados del comprador -> 97 restantes
        assertEquals(97, buyer.getCreditBalance());
        assertEquals(1, sellerA.getCreditBalance());
        assertEquals(2, sellerB.getCreditBalance());
        assertEquals(OrderStatus.COMPLETED, askA.getStatus());
        assertEquals(OrderStatus.COMPLETED, askB.getStatus());
        assertEquals(2, buyer.getTokens().size());

        verify(tokenRepository, times(1)).setTokenOwner(201L, 1L);
        verify(tokenRepository, times(1)).setTokenOwner(202L, 1L);
        verify(sellOrderRepository, times(1)).saveAll(List.of(askA, askB));
        verify(userRepository, times(1)).guardar(buyer);
        verify(userRepository, times(1)).guardar(sellerA);
        verify(userRepository, times(1)).guardar(sellerB);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Transaction>> transaccionesCaptor = ArgumentCaptor.forClass(List.class);
        verify(transactionRepository, times(1)).guardarTodos(transaccionesCaptor.capture());

        List<Transaction> transacciones = transaccionesCaptor.getValue();
        assertEquals(3, transacciones.size());

        // Transacción del comprador
        Transaction txComprador = transacciones.stream()
                .filter(t -> t.getType() == TransactionType.BUY)
                .findFirst()
                .orElseThrow();
        assertEquals(buyer, txComprador.getOwner());
        assertEquals(player, txComprador.getPlayer());
        assertEquals(2, txComprador.getQuantity());
        assertEquals(10.0, txComprador.getUnitPrice());
        assertEquals(20.0, txComprador.getTotalPrice());

        // Transacciones de los vendedores
        List<Transaction> txVentas = transacciones.stream()
                .filter(t -> t.getType() == TransactionType.SELL)
                .toList();
        assertEquals(2, txVentas.size());

        Transaction txSellerA = txVentas.stream()
                .filter(t -> t.getOwner().equals(sellerA))
                .findFirst()
                .orElseThrow();
        assertEquals(1, txSellerA.getQuantity());
        assertEquals(player, txSellerA.getPlayer());

        Transaction txSellerB = txVentas.stream()
                .filter(t -> t.getOwner().equals(sellerB))
                .findFirst()
                .orElseThrow();
        assertEquals(1, txSellerB.getQuantity());
        assertEquals(player, txSellerB.getPlayer());
    }

    @Test
    void executeMarketBuyConUnSoloVendedorAgrupaTransaccionDeVenta() {
        Player player = playerWithPrice(15);
        User buyer = userWithCredits(1L, 100);
        User seller = userWithCredits(2L, 0);

        SellOrder ask1 = sellOrder(101L, 2L, 201L, 5);
        SellOrder ask2 = sellOrder(102L, 2L, 202L, 5);
        SellOrder ask3 = sellOrder(103L, 2L, 203L, 5);

        when(userRepository.recuperar(1L)).thenReturn(Optional.of(buyer));
        when(userRepository.recuperar(2L)).thenReturn(Optional.of(seller));
        when(playerRepository.recuperar(PLAYER)).thenReturn(Optional.of(player));
        when(sellOrderRepository.findOpenSellOrders(PLAYER)).thenReturn(List.of(ask1, ask2, ask3));
        when(tokenRepository.findById(any())).thenReturn(Optional.of(new Token(1L, null, player)));

        service.executeMarketBuy(new BuyOrder(1L, PLAYER, 3));

        assertEquals(85, buyer.getCreditBalance());
        assertEquals(15, seller.getCreditBalance());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Transaction>> transaccionesCaptor = ArgumentCaptor.forClass(List.class);
        verify(transactionRepository, times(1)).guardarTodos(transaccionesCaptor.capture());

        List<Transaction> transacciones = transaccionesCaptor.getValue();
        // 1 transacción de BUY (3 tokens) y 1 de SELL agrupada (3 tokens)
        assertEquals(2, transacciones.size());

        Transaction txVenta = transacciones.stream()
                .filter(t -> t.getType() == TransactionType.SELL)
                .findFirst()
                .orElseThrow();
        assertEquals(seller, txVenta.getOwner());
        assertEquals(3, txVenta.getQuantity());
        assertEquals(player, txVenta.getPlayer());
    }

    @Test
    void executeMarketBuyFallaCuandoNoHaySuficienteLiquidez() {
        Player player = playerWithPrice(10);
        User buyer = userWithCredits(1L, 100);
        when(userRepository.recuperar(1L)).thenReturn(Optional.of(buyer));
        when(playerRepository.recuperar(PLAYER)).thenReturn(Optional.of(player));
        when(sellOrderRepository.findOpenSellOrders(PLAYER))
                .thenReturn(List.of(sellOrder(101L, 2L, 201L, 1)));

        BuyOrder order = new BuyOrder(1L, PLAYER, 5);
        assertThrows(InsufficientLiquidityException.class,
                () -> service.executeMarketBuy(order));

        verify(sellOrderRepository, never()).saveAll(any());
        verify(transactionRepository, never()).guardarTodos(any());
    }

    @Test
    void executeMarketBuyFallaPorFondosInsuficientesDelComprador() {
        Player player = playerWithPrice(10);
        User buyer = userWithCredits(1L, 1); // solo 1 crédito
        SellOrder ask = sellOrder(101L, 2L, 201L, 10); // cuesta 10

        when(userRepository.recuperar(1L)).thenReturn(Optional.of(buyer));
        when(userRepository.recuperar(2L)).thenReturn(Optional.of(userWithCredits(2L, 0)));
        when(playerRepository.recuperar(PLAYER)).thenReturn(Optional.of(player));
        when(sellOrderRepository.findOpenSellOrders(PLAYER)).thenReturn(List.of(ask));

        BuyOrder order = new BuyOrder(1L, PLAYER, 1);
        assertThrows(InsufficientFundsException.class,
                () -> service.executeMarketBuy(order));

        verify(sellOrderRepository, never()).saveAll(any());
        verify(transactionRepository, never()).guardarTodos(any());
    }

    @Test
    void executeMarketBuyIgnoraLasOrdenesDelPropioComprador() {
        Player player = playerWithPrice(10);
        User buyer = userWithCredits(1L, 100);
        when(userRepository.recuperar(1L)).thenReturn(Optional.of(buyer));
        when(playerRepository.recuperar(PLAYER)).thenReturn(Optional.of(player));
        // La única orden pertenece al propio comprador
        when(sellOrderRepository.findOpenSellOrders(PLAYER))
                .thenReturn(List.of(sellOrder(101L, 1L, 201L, 1)));

        BuyOrder order = new BuyOrder(1L, PLAYER, 1);
        assertThrows(InsufficientLiquidityException.class,
                () -> service.executeMarketBuy(order));

        verify(sellOrderRepository, never()).saveAll(any());
        verify(transactionRepository, never()).guardarTodos(any());
    }

    @Test
    void executeMarketBuyFallaSiElCompradorNoExiste() {
        when(userRepository.recuperar(1L)).thenReturn(Optional.empty());

        BuyOrder order = new BuyOrder(1L, PLAYER, 1);
        EntidadNoEncontradaException ex = assertThrows(EntidadNoEncontradaException.class,
                () -> service.executeMarketBuy(order));

        assertTrue(ex.getMessage().contains("User not found: 1"));
        verifyNoInteractions(playerRepository);
        verifyNoInteractions(sellOrderRepository);
    }

    @Test
    void executeMarketBuyFallaSiElJugadorNoExiste() {
        User buyer = userWithCredits(1L, 100);
        when(userRepository.recuperar(1L)).thenReturn(Optional.of(buyer));
        when(playerRepository.recuperar(PLAYER)).thenReturn(Optional.empty());

        BuyOrder order = new BuyOrder(1L, PLAYER, 1);
        EntidadNoEncontradaException ex = assertThrows(EntidadNoEncontradaException.class,
                () -> service.executeMarketBuy(order));

        assertTrue(ex.getMessage().contains("Player not found: 10"));
        verifyNoInteractions(sellOrderRepository);
    }

    @Test
    void executeMarketBuyFallaSiElVendedorNoExiste() {
        Player player = playerWithPrice(10);
        User buyer = userWithCredits(1L, 100);
        SellOrder ask = sellOrder(101L, 99L, 201L, 10);

        when(userRepository.recuperar(1L)).thenReturn(Optional.of(buyer));
        when(playerRepository.recuperar(PLAYER)).thenReturn(Optional.of(player));
        when(sellOrderRepository.findOpenSellOrders(PLAYER)).thenReturn(List.of(ask));
        when(userRepository.recuperar(99L)).thenReturn(Optional.empty());

        BuyOrder order = new BuyOrder(1L, PLAYER, 1);
        EntidadNoEncontradaException ex = assertThrows(EntidadNoEncontradaException.class,
                () -> service.executeMarketBuy(order));

        assertTrue(ex.getMessage().contains("Seller not found: 99"));
        verify(sellOrderRepository, never()).saveAll(any());
        verify(transactionRepository, never()).guardarTodos(any());
    }

    @Test
    void executeMarketBuyContinuaCuandoElTokenNoSeEncuentraEnElRepositorio() {
        Player player = playerWithPrice(10);
        User buyer = userWithCredits(1L, 100);
        User seller = userWithCredits(2L, 0);
        SellOrder ask = sellOrder(101L, 2L, 201L, 5);

        when(userRepository.recuperar(1L)).thenReturn(Optional.of(buyer));
        when(userRepository.recuperar(2L)).thenReturn(Optional.of(seller));
        when(playerRepository.recuperar(PLAYER)).thenReturn(Optional.of(player));
        when(sellOrderRepository.findOpenSellOrders(PLAYER)).thenReturn(List.of(ask));
        when(tokenRepository.findById(201L)).thenReturn(Optional.empty());

        service.executeMarketBuy(new BuyOrder(1L, PLAYER, 1));

        assertEquals(95, buyer.getCreditBalance());
        assertEquals(5, seller.getCreditBalance());
        assertEquals(OrderStatus.COMPLETED, ask.getStatus());
        // Como el token fue nulo, no se agregó al comprador pero la orden se completó y la transacción se guardó
        assertEquals(0, buyer.getTokens().size());

        verify(tokenRepository, times(1)).setTokenOwner(201L, 1L);
        verify(sellOrderRepository, times(1)).saveAll(List.of(ask));
        verify(transactionRepository, times(1)).guardarTodos(any());
    }
}
