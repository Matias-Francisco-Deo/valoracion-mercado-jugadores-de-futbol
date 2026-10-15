package com.overcode.service;

import com.overcode.model.BuyOrder;
import com.overcode.model.orden.OrderStatus;
import com.overcode.model.Player;
import com.overcode.model.SellOrder;
import com.overcode.model.Token;
import com.overcode.model.User;
import com.overcode.model.exception.InsufficientFundsException;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import com.overcode.persistence.repository.interfaces.SellOrderRepository;
import com.overcode.persistence.repository.interfaces.TokenRepository;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.service.exception.InsufficientLiquidityException;
import com.overcode.service.impl.P2POrderServiceImpl;
import org.junit.jupiter.api.Test;

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

    private final P2POrderServiceImpl service =
            new P2POrderServiceImpl(sellOrderRepository, userRepository, playerRepository, tokenRepository);

    private static final Long PLAYER = 10L;

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
    void marketBuySweepsMultipleOrdersAtMakerPrices() {
        User buyer = userWithCredits(1L, 100);
        User sellerA = userWithCredits(2L, 0);
        User sellerB = userWithCredits(3L, 0);

        SellOrder askA = sellOrder(101L, 2L, 201L, 1);
        SellOrder askB = sellOrder(102L, 3L, 202L, 2);

        when(userRepository.recuperar(1L)).thenReturn(Optional.of(buyer));
        when(userRepository.recuperar(2L)).thenReturn(Optional.of(sellerA));
        when(userRepository.recuperar(3L)).thenReturn(Optional.of(sellerB));
        when(sellOrderRepository.findOpenSellOrders(PLAYER)).thenReturn(List.of(askA, askB));
        when(tokenRepository.findById(201L)).thenReturn(Optional.of(new Token(201L, null, new Player())));
        when(tokenRepository.findById(202L)).thenReturn(Optional.of(new Token(202L, null, new Player())));

        service.executeMarketBuy(new BuyOrder(1L, PLAYER, 2));

        // 1 + 2 = 3 debited from buyer -> 97 remaining
        assertEquals(97, buyer.getCreditBalance());
        assertEquals(1, sellerA.getCreditBalance());
        assertEquals(2, sellerB.getCreditBalance());
        assertEquals(OrderStatus.COMPLETED, askA.getStatus());
        assertEquals(OrderStatus.COMPLETED, askB.getStatus());
        assertEquals(2, buyer.getTokens().size());

        verify(tokenRepository, times(1)).setTokenOwner(201L, 1L);
        verify(tokenRepository, times(1)).setTokenOwner(202L, 1L);
        verify(sellOrderRepository, times(1)).saveAll(List.of(askA, askB));
    }

    @Test
    void marketBuyFailsWhenBookTooThin() {
        User buyer = userWithCredits(1L, 100);
        when(userRepository.recuperar(1L)).thenReturn(Optional.of(buyer));
        when(sellOrderRepository.findOpenSellOrders(PLAYER))
                .thenReturn(List.of(sellOrder(101L, 2L, 201L, 1)));

        assertThrows(InsufficientLiquidityException.class,
                () -> service.executeMarketBuy(new BuyOrder(1L, PLAYER, 5)));

        verify(sellOrderRepository, never()).saveAll(any());
    }

    @Test
    void marketBuyFailsOnInsufficientFunds() {
        User buyer = userWithCredits(1L, 1); // solo 1 crédito
        SellOrder ask = sellOrder(101L, 2L, 201L, 10); // cuesta 10

        when(userRepository.recuperar(1L)).thenReturn(Optional.of(buyer));
        when(userRepository.recuperar(2L)).thenReturn(Optional.of(userWithCredits(2L, 0)));
        when(sellOrderRepository.findOpenSellOrders(PLAYER)).thenReturn(List.of(ask));

        assertThrows(InsufficientFundsException.class,
                () -> service.executeMarketBuy(new BuyOrder(1L, PLAYER, 1)));

        verify(sellOrderRepository, never()).saveAll(any());
    }

    @Test
    void marketBuyIgnoresOwnOrders() {
        User buyer = userWithCredits(1L, 100);
        when(userRepository.recuperar(1L)).thenReturn(Optional.of(buyer));
        // La única orden pertenece al propio comprador
        when(sellOrderRepository.findOpenSellOrders(PLAYER))
                .thenReturn(List.of(sellOrder(101L, 1L, 201L, 1)));

        assertThrows(InsufficientLiquidityException.class,
                () -> service.executeMarketBuy(new BuyOrder(1L, PLAYER, 1)));
    }
}
