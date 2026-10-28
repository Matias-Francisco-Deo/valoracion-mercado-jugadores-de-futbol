package com.overcode.service;

import com.overcode.model.exception.InsufficientFundsException;
import com.overcode.model.exception.InsufficientLiquidityException;
import com.overcode.model.player.Player;
import com.overcode.model.token.Token;
import com.overcode.model.user.User;
import com.overcode.model.venta.BuyOrder;
import com.overcode.model.venta.OrdenDeCompra;
import com.overcode.model.venta.orden.OrderStatus;
import com.overcode.model.venta.transaction.TransaccionFinanciera;
import com.overcode.model.venta.transaction.TransactionType;
import com.overcode.persistence.repository.interfaces.*;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.impl.TokenMarketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class TokenMarketServiceBuyTest {

    private final SellOrderRepository sellOrderRepository = mock(SellOrderRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final PlayerRepository playerRepository = mock(PlayerRepository.class);
    private final TokenRepository tokenRepository = mock(TokenRepository.class);
    private final TransactionRepository transactionRepository = mock(TransactionRepository.class);

    private final TokenMarketServiceImpl service =
            new TokenMarketServiceImpl(sellOrderRepository, userRepository, playerRepository, tokenRepository, transactionRepository);

    private static final Long PLAYER = 10L;

    @BeforeEach
    void setUpPlayerRepositoryDefaults() {
        when(playerRepository.existsById(anyLong())).thenReturn(true);
    }

    private Player playerWithPrice(Double price) {
        Player player = new Player();
        player.setId(PLAYER);
        player.setName("Lionel Messi");
        player.setCurrentPrice(price);
        return player;
    }

    private OrdenDeCompra sellOrder(Long id, Long userId, Long tokenId, Double price) {
        User seller = new User("user-" + userId, "user" + userId + "@gmail.com", "pass");
        seller.setId(userId);
        Player player = new Player();
        player.setId(PLAYER);
        Token token = new Token(tokenId, null, player);
        return new OrdenDeCompra(id, seller, player, token, price, OrderStatus.OPEN, Instant.now());
    }

    private OrdenDeCompra sellOrder(Long id, User seller, Player player, Long tokenId, Double price) {
        Token token = new Token(tokenId, null, player);
        return new OrdenDeCompra(id, seller, player, token, price, OrderStatus.OPEN, Instant.now());
    }

    private User userWithCredits(Long userId, Double credits) {
        User user = new User("user-" + userId, "user" + userId + "@gmail.com", "pass");
        user.setId(userId);
        if (credits > 0) {
            user.creditCredits(credits);
        }
        return user;
    }

    @Test
    void executeMarketBuyEjecutaCompraDeMultiplesOrdenesYGuardaTransacciones() {
        Player player = playerWithPrice(10d);
        User buyer = userWithCredits(1L, 100d);
        User sellerA = userWithCredits(2L, 0d);
        User sellerB = userWithCredits(3L, 0d);

        OrdenDeCompra askA = sellOrder(101L, sellerA, player, 201L, 1d);
        OrdenDeCompra askB = sellOrder(102L, sellerB, player, 202L, 2d);

        when(userRepository.recuperar(1L)).thenReturn(Optional.of(buyer));
        when(userRepository.recuperar(2L)).thenReturn(Optional.of(sellerA));
        when(userRepository.recuperar(3L)).thenReturn(Optional.of(sellerB));
        when(playerRepository.recuperar(PLAYER)).thenReturn(Optional.of(player));
        when(sellOrderRepository.findOpenSellOrders(PLAYER,buyer.getId())).thenReturn(List.of(askA, askB));

        service.buy(new BuyOrder(buyer, player, 2));

        // 1 + 2 = 3 debitados del comprador -> 97 restantes
        assertEquals(97, buyer.getCreditBalance());
        assertEquals(1, sellerA.getCreditBalance());
        assertEquals(2, sellerB.getCreditBalance());
        assertEquals(OrderStatus.COMPLETED, askA.getStatus());
        assertEquals(OrderStatus.COMPLETED, askB.getStatus());
        assertEquals(2, buyer.getTokens().size());

        verify(sellOrderRepository, times(1)).saveAll(List.of(askA, askB));
        verify(userRepository, times(1)).guardar(buyer);
        verify(userRepository, times(1)).guardar(sellerA);
        verify(userRepository, times(1)).guardar(sellerB);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<TransaccionFinanciera>> transaccionesCaptor = ArgumentCaptor.forClass(List.class);
        verify(transactionRepository, times(1)).guardarTodos(transaccionesCaptor.capture());

        List<TransaccionFinanciera> transacciones = transaccionesCaptor.getValue();
        assertEquals(3, transacciones.size());

        // Transacción del comprador
        TransaccionFinanciera txComprador = transacciones.stream()
                .filter(t -> t.getType() == TransactionType.BUY)
                .findFirst()
                .orElseThrow();
        assertEquals(buyer, txComprador.getOwner());
        assertEquals(PLAYER, txComprador.getPlayer().getId());
        assertEquals(2, txComprador.getQuantity());
        assertEquals(10.0, txComprador.getUnitPrice());
        assertEquals(20.0, txComprador.getTotalPrice());

        // Transacciones de los vendedores
        List<TransaccionFinanciera> txVentas = transacciones.stream()
                .filter(t -> t.getType() == TransactionType.SELL)
                .toList();
        assertEquals(2, txVentas.size());

        TransaccionFinanciera txSellerA = txVentas.stream()
                .filter(t -> t.getOwner().equals(sellerA))
                .findFirst()
                .orElseThrow();
        assertEquals(1, txSellerA.getQuantity());
        assertEquals(PLAYER, txSellerA.getPlayer().getId());

        TransaccionFinanciera txSellerB = txVentas.stream()
                .filter(t -> t.getOwner().equals(sellerB))
                .findFirst()
                .orElseThrow();
        assertEquals(1, txSellerB.getQuantity());
        assertEquals(PLAYER, txSellerB.getPlayer().getId());
    }

    @Test
    void executeMarketBuyConUnSoloVendedorAgrupaTransaccionDeVenta() {
        Player player = playerWithPrice(15d);
        User buyer = userWithCredits(1L, 100d);
        User seller = userWithCredits(2L, 0d);

        OrdenDeCompra ask1 = sellOrder(101L, seller, player, 201L, 5d);
        OrdenDeCompra ask2 = sellOrder(102L, seller, player, 202L, 5d);
        OrdenDeCompra ask3 = sellOrder(103L, seller, player, 203L, 5d);

        when(userRepository.recuperar(1L)).thenReturn(Optional.of(buyer));
        when(userRepository.recuperar(2L)).thenReturn(Optional.of(seller));
        when(playerRepository.recuperar(PLAYER)).thenReturn(Optional.of(player));
        when(sellOrderRepository.findOpenSellOrders(PLAYER,buyer.getId())).thenReturn(List.of(ask1, ask2, ask3));

        service.buy(new BuyOrder(buyer, player, 3));

        assertEquals(85, buyer.getCreditBalance());
        assertEquals(15, seller.getCreditBalance());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<TransaccionFinanciera>> transaccionesCaptor = ArgumentCaptor.forClass(List.class);
        verify(transactionRepository, times(1)).guardarTodos(transaccionesCaptor.capture());

        List<TransaccionFinanciera> transacciones = transaccionesCaptor.getValue();
        // 1 transacción de BUY (3 tokens) y 1 de SELL agrupada (3 tokens)
        assertEquals(2, transacciones.size());

        TransaccionFinanciera txVenta = transacciones.stream()
                .filter(t -> t.getType() == TransactionType.SELL)
                .findFirst()
                .orElseThrow();
        assertEquals(seller, txVenta.getOwner());
        assertEquals(3, txVenta.getQuantity());
        assertEquals(PLAYER, txVenta.getPlayer().getId());
    }

    @Test
    void executeMarketBuyFallaCuandoNoHaySuficienteLiquidez() {
        Player player = playerWithPrice(10d);
        User buyer = userWithCredits(1L, 100d);
        when(userRepository.recuperar(1L)).thenReturn(Optional.of(buyer));
        when(playerRepository.recuperar(PLAYER)).thenReturn(Optional.of(player));
        when(sellOrderRepository.findOpenSellOrders(PLAYER,buyer.getId()))
                .thenReturn(List.of(sellOrder(101L, 2L, 201L, 1d)));

        BuyOrder order = new BuyOrder(buyer, player, 5);
        assertThrows(InsufficientLiquidityException.class,
                () -> service.buy(order));

        verify(sellOrderRepository, never()).saveAll(any());
        verify(transactionRepository, never()).guardarTodos(any());
    }

    @Test
    void executeMarketBuyFallaPorFondosInsuficientesDelComprador() {
        Player player = playerWithPrice(10d);
        User buyer = userWithCredits(1L, 1d); // solo 1 crédito
        OrdenDeCompra ask = sellOrder(101L, 2L, 201L, 10d); // cuesta 10

        when(userRepository.recuperar(1L)).thenReturn(Optional.of(buyer));
        when(userRepository.recuperar(2L)).thenReturn(Optional.of(userWithCredits(2L, 0d)));
        when(playerRepository.recuperar(PLAYER)).thenReturn(Optional.of(player));
        when(sellOrderRepository.findOpenSellOrders(PLAYER,buyer.getId())).thenReturn(List.of(ask));

        BuyOrder order = new BuyOrder(buyer, player, 1);
        assertThrows(InsufficientFundsException.class,
                () -> service.buy(order));

        verify(sellOrderRepository, never()).saveAll(any());
        verify(transactionRepository, never()).guardarTodos(any());
    }

    @Test
    void executeMarketBuyIgnoraLasOrdenesDelPropioComprador() {
        Player player = playerWithPrice(10d);
        User buyer = userWithCredits(1L, 100d);
        when(userRepository.recuperar(1L)).thenReturn(Optional.of(buyer));
        when(playerRepository.recuperar(PLAYER)).thenReturn(Optional.of(player));
        // La única orden pertenece al propio comprador
        when(sellOrderRepository.findOpenSellOrders(PLAYER,buyer.getId()))
                .thenReturn(List.of());

        BuyOrder order = new BuyOrder(buyer, player, 1);
        assertThrows(InsufficientLiquidityException.class,
                () -> service.buy(order));

        verify(sellOrderRepository, never()).saveAll(any());
        verify(transactionRepository, never()).guardarTodos(any());
    }

    @Test
    void executeMarketBuyFallaSiElCompradorNoExiste() {
        when(userRepository.recuperar(1L)).thenReturn(Optional.empty());

        User ghostBuyer = new User("ghost", "ghost@gmail.com", "pass");
        ghostBuyer.setId(1L);
        BuyOrder order = new BuyOrder(ghostBuyer, new Player(), 1);
        assertThrows(EntidadNoEncontradaException.class,
                () -> service.buy(order));
    }

    @Test
    void executeMarketBuyFallaSiElJugadorNoExiste() {
        Player player = playerWithPrice(10d);
        User buyer = userWithCredits(1L, 100d);
        when(userRepository.recuperar(1L)).thenReturn(Optional.of(buyer));
        when(playerRepository.existsById(PLAYER)).thenReturn(false);
        when(playerRepository.recuperar(PLAYER)).thenReturn(Optional.empty());

        BuyOrder order = new BuyOrder(buyer, player, 1);
        assertThrows(EntidadNoEncontradaException.class,
                () -> service.buy(order));
    }

}
