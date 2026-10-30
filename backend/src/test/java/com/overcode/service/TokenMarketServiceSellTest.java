package com.overcode.service;

import com.overcode.model.exception.OperacionInvalidaOrdenException;
import com.overcode.model.exception.TokensInsuficientesParaVenderException;
import com.overcode.model.market.PeticionVenta;
import com.overcode.model.market.orden.OrdenDeVenta;
import com.overcode.model.market.orden.OrderStatus;
import com.overcode.model.player.Player;
import com.overcode.model.token.Token;
import com.overcode.model.user.User;
import com.overcode.persistence.repository.interfaces.*;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.exception.OrderNotFoundException;
import com.overcode.service.exception.venta.ExisteOrdenAbiertaParaTokenException;
import com.overcode.service.impl.TokenMarketServiceImpl;
import com.overcode.service.interfaces.UserService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TokenMarketServiceSellTest {

    private final OrdenDeCompraRepository ordenDeCompraRepository = mock(OrdenDeCompraRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final PlayerRepository playerRepository = mock(PlayerRepository.class);
    private final TokenRepository tokenRepository = mock(TokenRepository.class);
    private final TransactionRepository transactionRepository = mock(TransactionRepository.class);
    private final UserService userService = mock(UserService.class);

    private final TokenMarketServiceImpl service =
            new TokenMarketServiceImpl(ordenDeCompraRepository, userRepository, playerRepository, tokenRepository, transactionRepository, userService);

    private static final Long USER_ID = 1L;
    private static final Long PLAYER_ID = 10L;

    private Player playerWithPrice(Double price) {
        Player player = new Player();
        player.setId(PLAYER_ID);
        player.setCurrentPrice(price);
        return player;
    }

    private User userWithTokens(int quantity) {
        User user = new User("user1", "user1@gmail.com", "pass");
        user.setId(USER_ID);
        Player player = playerWithPrice(25d);
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
        Player player = playerWithPrice(25d);
        User user = userWithTokens(10);

        user.getTokens().forEach(token -> token.setPlayer(player));
        player.setTokens(new ArrayList<>(user.getTokens()));

        when(playerRepository.recuperar(PLAYER_ID)).thenReturn(Optional.of(player));
        when(userRepository.recuperar(USER_ID)).thenReturn(Optional.of(user));
        when(ordenDeCompraRepository.saveAll(any())).thenAnswer(i -> i.getArgument(0));

        List<OrdenDeVenta> orders =
                service.sell(new PeticionVenta(user, player, 6));

        assertEquals(6, orders.size());

        for (OrdenDeVenta order : orders) {
            assertEquals(OrderStatus.OPEN, order.getStatus());
            assertEquals(25, order.getPrice());
            assertEquals(USER_ID, order.getOwner().getId());
            assertEquals(PLAYER_ID, order.getPlayer().getId());
            assertNotNull(order.getToken().getId());
        }

        assertEquals(4, user.getTokenCountForPlayer(PLAYER_ID));

        verify(userRepository, times(1)).guardar(user);
        verify(ordenDeCompraRepository, times(1)).saveAll(any());
    }

    @Test
    void createSellOrdersFallaSiElJugadorNoExiste() {
        when(playerRepository.recuperar(PLAYER_ID)).thenReturn(Optional.empty());

        PeticionVenta post = new PeticionVenta(new User(USER_ID), new Player(PLAYER_ID), 5);
        assertThrows(EntidadNoEncontradaException.class,
                () -> service.sell(post));

        verifyNoInteractions(userRepository);
        verifyNoInteractions(ordenDeCompraRepository);
    }

    @Test
    void createSellOrdersFallaSiElUsuarioNoExiste() {
        when(playerRepository.recuperar(PLAYER_ID)).thenReturn(Optional.of(playerWithPrice(25d)));
        when(userRepository.recuperar(USER_ID)).thenReturn(Optional.empty());

        PeticionVenta post = new PeticionVenta(new User(USER_ID), new Player(PLAYER_ID), 5);
        assertThrows(EntidadNoEncontradaException.class,
                () -> service.sell(post));

        verifyNoInteractions(ordenDeCompraRepository);
    }

    @Test
    void createSellOrdersFallaSiNoTieneTokensDeEseJugador() {
        User user = userWithTokens(0);
        when(playerRepository.recuperar(PLAYER_ID)).thenReturn(Optional.of(playerWithPrice(25d)));
        when(userRepository.recuperar(USER_ID)).thenReturn(Optional.of(user));

        PeticionVenta post = new PeticionVenta(new User(USER_ID), new Player(PLAYER_ID), 5);
        assertThrows(TokensInsuficientesParaVenderException.class,
                () -> service.sell(post));

        verifyNoInteractions(ordenDeCompraRepository);
    }

    @Test
    void createSellOrdersFallaSiNoAlcanzaLaCantidadDeTokens() {
        User user = userWithTokens(3);
        Player player = playerWithPrice(25d);
        when(playerRepository.recuperar(PLAYER_ID)).thenReturn(Optional.of(player));
        when(userRepository.recuperar(USER_ID)).thenReturn(Optional.of(user));

        PeticionVenta post = new PeticionVenta(user, player, 5);
        assertThrows(TokensInsuficientesParaVenderException.class,
                () -> service.sell(post));

        verifyNoInteractions(ordenDeCompraRepository);
    }

    @Test
    void createSellOrdersFallaSiElTokenYaTieneUnaOrdenAbierta() {
        Player player = playerWithPrice(25d);
        User user = userWithTokens(1);
        user.getTokens().forEach(token -> token.setPlayer(player));
        player.setTokens(new ArrayList<>(user.getTokens()));

        when(playerRepository.recuperar(PLAYER_ID)).thenReturn(Optional.of(player));
        when(userRepository.recuperar(USER_ID)).thenReturn(Optional.of(user));
        when(ordenDeCompraRepository.existsOpenOrderByTokenId(1L)).thenReturn(true);

        PeticionVenta post = new PeticionVenta(user, player, 1);
        assertThrows(ExisteOrdenAbiertaParaTokenException.class,
                () -> service.sell(post));

        verify(ordenDeCompraRepository).existsOpenOrderByTokenId(1L);
        verify(ordenDeCompraRepository, never()).saveAll(any());
        verify(userRepository, never()).guardar(any());
        verify(playerRepository, never()).guardar(any());
    }

    // ------------------------------ cancelOrder ------------------------------

    @Test
    @Disabled("Esta feature no está en funcionamiento todavía")
    void cancelOrderCancelaYDevuelveTokenAlVendedor() {
        User userTest = userWithTokens(0);
        Player playerTest = playerWithPrice(25d);
        Token tokenTest = new Token(55L,userTest,playerTest);

        OrdenDeVenta order = new OrdenDeVenta(100L, userTest, playerTest, tokenTest, 25d, OrderStatus.OPEN, Instant.now());
        when(ordenDeCompraRepository.recuperarPorId(100L)).thenReturn(Optional.of(order));

        service.cancel(100L, USER_ID);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        verify(ordenDeCompraRepository, times(1)).save(order);
        verify(tokenRepository, times(1)).setTokenOwner(55L, USER_ID);
    }

    @Test
    void cancelOrderFallaSiLaOrdenNoExiste() {
        when(ordenDeCompraRepository.recuperarPorId(-1L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> service.cancel(-1L, USER_ID));
    }

    @Test
    @Disabled("Esta feature no está en funcionamiento todavía")
    void cancelOrderFallaSiNoEsElDueño() {
        User userTest = userWithTokens(0);
        Player playerTest = playerWithPrice(25d);
        Token tokenTest = new Token(55L,userTest,playerTest);

        OrdenDeVenta order = new OrdenDeVenta(100L, userTest, playerTest, tokenTest, 25d, OrderStatus.OPEN, Instant.now());
        when(ordenDeCompraRepository.recuperarPorId(100L)).thenReturn(Optional.of(order));

        assertThrows(OperacionInvalidaOrdenException.class, () -> service.cancel(100L, 999L));
    }
}
