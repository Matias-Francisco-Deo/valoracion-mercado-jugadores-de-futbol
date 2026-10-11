package com.overcode.service;

import com.overcode.model.OrderStatus;
import com.overcode.model.OrderType;
import com.overcode.model.P2POrder;
import com.overcode.model.Portfolio;
import com.overcode.persistence.repository.interfaces.AuditLogRepository;
import com.overcode.persistence.repository.interfaces.P2POrderRepository;
import com.overcode.persistence.repository.interfaces.PortfolioRepository;
import com.overcode.service.exception.InsufficientEscrowException;
import com.overcode.service.exception.InsufficientLiquidityException;
import com.overcode.service.impl.P2POrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class P2POrderServiceMarketTest {

    private final P2POrderRepository orderRepository = mock(P2POrderRepository.class);
    private final PortfolioRepository portfolioRepository = mock(PortfolioRepository.class);
    private final AuditLogRepository auditLogRepository = mock(AuditLogRepository.class);
    private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);

    private final P2POrderServiceImpl service =
            new P2POrderServiceImpl(orderRepository, portfolioRepository, auditLogRepository, eventPublisher);

    private static final Long PLAYER = 10L;

    private P2POrder order(Long userId, OrderType type, int qty, String price) {
        return new P2POrder(UUID.randomUUID(), userId, PLAYER, type, qty,
                new BigDecimal(price), OrderStatus.OPEN, Instant.now(), 0L);
    }

    private Portfolio portfolio(Long userId, String credits) {
        return new Portfolio(UUID.randomUUID(), userId, new BigDecimal(credits), 0L);
    }

    private void returnSavedArg() {
        when(portfolioRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void marketBuySweepsMultipleOrdersAtMakerPrices() {
        returnSavedArg();
        Portfolio buyer = portfolio(1L, "100");
        Portfolio sellerA = portfolio(2L, "0");
        Portfolio sellerB = portfolio(3L, "0");
        P2POrder askA = order(2L, OrderType.SELL, 5, "1.00");
        P2POrder askB = order(3L, OrderType.SELL, 10, "2.00");

        when(portfolioRepository.findByUserId(1L)).thenReturn(Optional.of(buyer));
        when(portfolioRepository.findByUserId(2L)).thenReturn(Optional.of(sellerA));
        when(portfolioRepository.findByUserId(3L)).thenReturn(Optional.of(sellerB));
        when(orderRepository.findOpenSellOrders(PLAYER)).thenReturn(List.of(askA, askB));

        service.executeMarketBuy(1L, PLAYER, 12);

        // 5*1.00 + 7*2.00 = 19.00 debited from buyer
        assertEquals(0, new BigDecimal("81.00").compareTo(buyer.getCredits()));
        assertEquals(12, buyer.getHoldingForPlayer(PLAYER).orElseThrow().getQuantity());
        assertEquals(0, new BigDecimal("5.00").compareTo(sellerA.getCredits()));
        assertEquals(0, new BigDecimal("14.00").compareTo(sellerB.getCredits()));
        assertEquals(OrderStatus.COMPLETED, askA.getStatus());
        assertEquals(3, askB.getQuantity());
        assertTrue(askB.isOpen());
        verify(auditLogRepository, times(2)).save(any());
    }

    @Test
    void marketBuyFailsWhenBookTooThin() {
        Portfolio buyer = portfolio(1L, "100");
        when(portfolioRepository.findByUserId(1L)).thenReturn(Optional.of(buyer));
        when(orderRepository.findOpenSellOrders(PLAYER))
                .thenReturn(List.of(order(2L, OrderType.SELL, 5, "1.00")));

        assertThrows(InsufficientLiquidityException.class,
                () -> service.executeMarketBuy(1L, PLAYER, 20));

        verify(portfolioRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void marketBuyFailsOnInsufficientFunds() {
        Portfolio buyer = portfolio(1L, "3");
        when(portfolioRepository.findByUserId(1L)).thenReturn(Optional.of(buyer));
        when(orderRepository.findOpenSellOrders(PLAYER))
                .thenReturn(List.of(order(2L, OrderType.SELL, 10, "2.00")));

        assertThrows(IllegalStateException.class,
                () -> service.executeMarketBuy(1L, PLAYER, 5));

        verify(orderRepository, never()).save(any());
    }

    @Test
    void marketBuyIgnoresOwnOrders() {
        when(portfolioRepository.findByUserId(1L)).thenReturn(Optional.of(portfolio(1L, "100")));
        // The only resting ask belongs to the taker itself.
        when(orderRepository.findOpenSellOrders(PLAYER))
                .thenReturn(List.of(order(1L, OrderType.SELL, 5, "1.00")));

        assertThrows(InsufficientLiquidityException.class,
                () -> service.executeMarketBuy(1L, PLAYER, 5));
    }

    @Test
    void marketSellSweepsBidsHighestFirst() {
        returnSavedArg();
        Portfolio seller = portfolio(1L, "0");
        seller.getOrCreateHoldingForPlayer(PLAYER).addTokens(10);
        Portfolio buyerA = portfolio(2L, "0");
        Portfolio buyerB = portfolio(3L, "0");
        P2POrder bidA = order(2L, OrderType.BUY, 5, "3.00");
        P2POrder bidB = order(3L, OrderType.BUY, 10, "2.00");

        when(portfolioRepository.findByUserId(1L)).thenReturn(Optional.of(seller));
        when(portfolioRepository.findByUserId(2L)).thenReturn(Optional.of(buyerA));
        when(portfolioRepository.findByUserId(3L)).thenReturn(Optional.of(buyerB));
        when(orderRepository.findOpenBuyOrders(PLAYER)).thenReturn(List.of(bidA, bidB));

        service.executeMarketSell(1L, PLAYER, 8);

        // 5*3.00 + 3*2.00 = 21.00 credited to seller; 8 tokens delivered
        assertEquals(0, new BigDecimal("21.00").compareTo(seller.getCredits()));
        assertEquals(2, seller.getHoldingForPlayer(PLAYER).orElseThrow().getQuantity());
        assertEquals(5, buyerA.getHoldingForPlayer(PLAYER).orElseThrow().getQuantity());
        assertEquals(3, buyerB.getHoldingForPlayer(PLAYER).orElseThrow().getQuantity());
        assertEquals(OrderStatus.COMPLETED, bidA.getStatus());
        assertEquals(7, bidB.getQuantity());
        verify(auditLogRepository, times(2)).save(any());
    }

    @Test
    void marketSellFailsWhenSellerLacksTokens() {
        Portfolio seller = portfolio(1L, "0");
        seller.getOrCreateHoldingForPlayer(PLAYER).addTokens(3);
        when(portfolioRepository.findByUserId(1L)).thenReturn(Optional.of(seller));

        assertThrows(InsufficientEscrowException.class,
                () -> service.executeMarketSell(1L, PLAYER, 8));

        verify(portfolioRepository, never()).save(any());
    }
}
