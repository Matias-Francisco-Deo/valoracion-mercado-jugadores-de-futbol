package com.overcode.service;

import com.overcode.model.OrderStatus;
import com.overcode.model.OrderType;
import com.overcode.model.P2POrder;
import com.overcode.model.Player;
import com.overcode.model.Portfolio;
import com.overcode.persistence.repository.interfaces.P2POrderRepository;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import com.overcode.persistence.repository.interfaces.PortfolioRepository;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.exception.InsufficientEscrowException;
import com.overcode.service.exception.OrderNotFoundException;
import com.overcode.service.exception.PortfolioNotFoundException;
import com.overcode.service.impl.P2POrderServiceImpl;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class P2POrderServiceSellTest {

    private final P2POrderRepository orderRepository = mock(P2POrderRepository.class);
    private final PortfolioRepository portfolioRepository = mock(PortfolioRepository.class);
    private final PlayerRepository playerRepository = mock(PlayerRepository.class);

    private final P2POrderServiceImpl service =
            new P2POrderServiceImpl(orderRepository, portfolioRepository, playerRepository);

    private static final Long USER_ID = 1L;
    private static final Long PLAYER_ID = 10L;

    private Player playerWithPrice(int price) {
        Player player = new Player();
        player.setId(PLAYER_ID);
        player.setCurrentPrice(price);
        return player;
    }

    private Portfolio portfolioWithTokens(int quantity) {
        Portfolio portfolio = new Portfolio(UUID.randomUUID(), USER_ID, BigDecimal.ZERO, 0L);
        if (quantity > 0) {
            portfolio.getOrCreateHoldingForPlayer(PLAYER_ID).addTokens(quantity);
        }
        return portfolio;
    }

    private void returnSavedArgs() {
        when(portfolioRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    }

    // ------------------------------ createSellOrder ------------------------------

    @Test
    void createSellOrderEscrowaTokensYCongelaElPrecioActualDelJugador() {
        returnSavedArgs();
        Portfolio portfolio = portfolioWithTokens(10);
        when(playerRepository.recuperar(PLAYER_ID)).thenReturn(Optional.of(playerWithPrice(25)));
        when(portfolioRepository.findByUserId(USER_ID)).thenReturn(Optional.of(portfolio));

        P2POrder order = service.createSellOrder(USER_ID, PLAYER_ID, 6);

        assertEquals(OrderType.SELL, order.getType());
        assertEquals(OrderStatus.OPEN, order.getStatus());
        assertEquals(6, order.getQuantity());
        assertEquals(0, new BigDecimal("25").compareTo(order.getPricePerToken()));
        // Tokens were escrowed out of the seller's holding immediately.
        assertEquals(4, portfolio.getHoldingForPlayer(PLAYER_ID).orElseThrow().getQuantity());
        verify(orderRepository, times(1)).save(any());
    }

    @Test
    void createSellOrderFallaSiElJugadorNoExiste() {
        when(playerRepository.recuperar(PLAYER_ID)).thenReturn(Optional.empty());

        assertThrows(EntidadNoEncontradaException.class,
                () -> service.createSellOrder(USER_ID, PLAYER_ID, 5));

        verifyNoInteractions(portfolioRepository);
        verifyNoInteractions(orderRepository);
    }

    @Test
    void createSellOrderFallaSiElPortfolioNoExiste() {
        when(playerRepository.recuperar(PLAYER_ID)).thenReturn(Optional.of(playerWithPrice(25)));
        when(portfolioRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        assertThrows(PortfolioNotFoundException.class,
                () -> service.createSellOrder(USER_ID, PLAYER_ID, 5));

        verifyNoInteractions(orderRepository);
    }

    @Test
    void createSellOrderFallaSiNoTieneTokensDeEseJugador() {
        Portfolio portfolio = portfolioWithTokens(0);
        when(playerRepository.recuperar(PLAYER_ID)).thenReturn(Optional.of(playerWithPrice(25)));
        when(portfolioRepository.findByUserId(USER_ID)).thenReturn(Optional.of(portfolio));

        assertThrows(InsufficientEscrowException.class,
                () -> service.createSellOrder(USER_ID, PLAYER_ID, 5));

        verifyNoInteractions(orderRepository);
    }

    @Test
    void createSellOrderFallaSiNoAlcanzaLaCantidadDeTokens() {
        Portfolio portfolio = portfolioWithTokens(3);
        when(playerRepository.recuperar(PLAYER_ID)).thenReturn(Optional.of(playerWithPrice(25)));
        when(portfolioRepository.findByUserId(USER_ID)).thenReturn(Optional.of(portfolio));

        assertThrows(InsufficientEscrowException.class,
                () -> service.createSellOrder(USER_ID, PLAYER_ID, 5));

        verifyNoInteractions(orderRepository);
    }

    @Test
    void createSellOrderFallaConCantidadNoPositiva() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createSellOrder(USER_ID, PLAYER_ID, 0));

        verifyNoInteractions(playerRepository);
        verifyNoInteractions(portfolioRepository);
    }

    // ------------------------------ cancelOrder ------------------------------

    private P2POrder openSellOrder(int quantity) {
        return P2POrder.builder()
                .id(UUID.randomUUID())
                .userId(USER_ID)
                .playerId(PLAYER_ID)
                .type(OrderType.SELL)
                .quantity(quantity)
                .pricePerToken(new BigDecimal("25"))
                .status(OrderStatus.OPEN)
                .createdAt(Instant.now())
                .version(0L)
                .build();
    }

    @Test
    void cancelOrderRefundaLosTokensEscrowedosYMarcaCancelled() {
        returnSavedArgs();
        P2POrder order = openSellOrder(6);
        Portfolio portfolio = portfolioWithTokens(0);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(portfolioRepository.findByUserId(USER_ID)).thenReturn(Optional.of(portfolio));

        service.cancelOrder(order.getId(), USER_ID);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(6, portfolio.getHoldingForPlayer(PLAYER_ID).orElseThrow().getQuantity());
    }

    @Test
    void cancelOrderFallaSiLaOrdenNoExiste() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> service.cancelOrder(orderId, USER_ID));
    }

    @Test
    void cancelOrderFallaSiNoEsElDueño() {
        P2POrder order = openSellOrder(6);
        UUID orderId = order.getId();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () -> service.cancelOrder(orderId, 999L));
    }

    @Test
    void cancelOrderFallaSiLaOrdenYaNoEstaAbierta() {
        P2POrder order = openSellOrder(6);
        order.cancel();
        UUID orderId = order.getId();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () -> service.cancelOrder(orderId, USER_ID));
    }
}
