package com.overcode.service;

import com.overcode.model.SellOrder;
import com.overcode.model.SellPost;
import com.overcode.model.User;
import com.overcode.model.orden.OrderStatus;
import com.overcode.model.player.Player;
import com.overcode.model.token.Token;
import com.overcode.persistence.repository.interfaces.*;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.exception.InsufficientEscrowException;
import com.overcode.service.exception.OrderNotFoundException;
import com.overcode.service.impl.P2POrderServiceImpl;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class P2POrderServiceSellTest {

    private final SellOrderRepository sellOrderRepository = mock(SellOrderRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final PlayerRepository playerRepository = mock(PlayerRepository.class);
    private final TokenRepository tokenRepository = mock(TokenRepository.class);
    private final TransactionRepository transactionRepository = mock(TransactionRepository.class);

    private final P2POrderServiceImpl service =
            new P2POrderServiceImpl(sellOrderRepository, userRepository, playerRepository, tokenRepository,transactionRepository);

    private static final Long USER_ID = 1L;
    private static final Long PLAYER_ID = 10L;

    private Player playerWithPrice(int price) {
        Player player = new Player();
        player.setId(PLAYER_ID);
        player.setCurrentPrice(price);
        return player;
    }

    private User userWithTokens(int quantity) {
        User user = new User("user1", "user1@gmail.com", "pass");
        user.setId(USER_ID);
        Player player = playerWithPrice(25);
        List<Token> tokens = new ArrayList<>();
        for (long i = 1; i <= quantity; i++) {
            tokens.add(new Token(i, user, player));
        }
        user.setTokens(tokens);
        return user;
    }

    // ------------------------------ createSellOrders ------------------------------

    @Test
    void createSellOrdersCreaUnaOrdenPorTokenYGuardaEnRepositorio() {
        Player player = playerWithPrice(25);
        User user = userWithTokens(10);

        user.getTokens().forEach(token -> token.setPlayer(player));
        player.setTokens(new ArrayList<>(user.getTokens()));

        when(playerRepository.recuperar(PLAYER_ID)).thenReturn(Optional.of(player));
        when(userRepository.recuperar(USER_ID)).thenReturn(Optional.of(user));
        when(sellOrderRepository.saveAll(any())).thenAnswer(i -> i.getArgument(0));

        List<SellOrder> orders =
                service.createSellOrders(new SellPost(USER_ID, PLAYER_ID, 6));

        assertEquals(6, orders.size());

        for (SellOrder order : orders) {
            assertEquals(OrderStatus.OPEN, order.getStatus());
            assertEquals(25, order.getPrice());
            assertEquals(USER_ID, order.getUserId());
            assertEquals(PLAYER_ID, order.getPlayerId());
            assertNotNull(order.getTokenId());
        }

        assertEquals(4, user.getTokenCountForPlayer(PLAYER_ID));

        verify(userRepository, times(1)).guardar(user);
        verify(sellOrderRepository, times(1)).saveAll(any());
    }

    @Test
    void createSellOrdersFallaSiElJugadorNoExiste() {
        when(playerRepository.recuperar(PLAYER_ID)).thenReturn(Optional.empty());

        SellPost post = new SellPost(USER_ID, PLAYER_ID, 5);
        assertThrows(EntidadNoEncontradaException.class,
                () -> service.createSellOrders(post));

        verifyNoInteractions(userRepository);
        verifyNoInteractions(sellOrderRepository);
    }

    @Test
    void createSellOrdersFallaSiElUsuarioNoExiste() {
        when(playerRepository.recuperar(PLAYER_ID)).thenReturn(Optional.of(playerWithPrice(25)));
        when(userRepository.recuperar(USER_ID)).thenReturn(Optional.empty());

        SellPost post = new SellPost(USER_ID, PLAYER_ID, 5);
        assertThrows(EntidadNoEncontradaException.class,
                () -> service.createSellOrders(post));

        verifyNoInteractions(sellOrderRepository);
    }

    @Test
    void createSellOrdersFallaSiNoTieneTokensDeEseJugador() {
        User user = userWithTokens(0);
        when(playerRepository.recuperar(PLAYER_ID)).thenReturn(Optional.of(playerWithPrice(25)));
        when(userRepository.recuperar(USER_ID)).thenReturn(Optional.of(user));

        SellPost post = new SellPost(USER_ID, PLAYER_ID, 5);
        assertThrows(InsufficientEscrowException.class,
                () -> service.createSellOrders(post));

        verifyNoInteractions(sellOrderRepository);
    }

    @Test
    void createSellOrdersFallaSiNoAlcanzaLaCantidadDeTokens() {
        User user = userWithTokens(3);
        when(playerRepository.recuperar(PLAYER_ID)).thenReturn(Optional.of(playerWithPrice(25)));
        when(userRepository.recuperar(USER_ID)).thenReturn(Optional.of(user));

        SellPost post = new SellPost(USER_ID, PLAYER_ID, 5);
        assertThrows(InsufficientEscrowException.class,
                () -> service.createSellOrders(post));

        verifyNoInteractions(sellOrderRepository);
    }

    // ------------------------------ cancelOrder ------------------------------

    @Test
    void cancelOrderCancelaYDevuelveTokenAlVendedor() {
        SellOrder order = new SellOrder(100L, USER_ID, PLAYER_ID, 55L, 25, OrderStatus.OPEN, Instant.now());
        when(sellOrderRepository.findById(100L)).thenReturn(Optional.of(order));

        service.cancelOrder(100L, USER_ID);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        verify(sellOrderRepository, times(1)).save(order);
        verify(tokenRepository, times(1)).setTokenOwner(55L, USER_ID);
    }

    @Test
    void cancelOrderFallaSiLaOrdenNoExiste() {
        when(sellOrderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> service.cancelOrder(999L, USER_ID));
    }

    @Test
    void cancelOrderFallaSiNoEsElDueño() {
        SellOrder order = new SellOrder(100L, USER_ID, PLAYER_ID, 55L, 25, OrderStatus.OPEN, Instant.now());
        when(sellOrderRepository.findById(100L)).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () -> service.cancelOrder(100L, 999L));
    }
}
