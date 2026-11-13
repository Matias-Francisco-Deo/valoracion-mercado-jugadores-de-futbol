package com.overcode.service;

import com.overcode.model.exception.TokensInsuficientesParaVenderException;
import com.overcode.model.market.PeticionVenta;
import com.overcode.model.player.Player;
import com.overcode.model.token.Token;
import com.overcode.model.user.User;
import com.overcode.persistence.repository.interfaces.*;
import com.overcode.service.exception.venta.ExisteOrdenAbiertaParaTokenException;
import com.overcode.service.impl.TokenMarketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class MarketSaleSelectionTest {
    private final OrdenDeCompraRepository orders = mock(OrdenDeCompraRepository.class);
    private final UserRepository users = mock(UserRepository.class);
    private final PlayerRepository players = mock(PlayerRepository.class);
    private final TransactionRepository transactions = mock(TransactionRepository.class);
    private final TokenMarketServiceImpl market = new TokenMarketServiceImpl(orders, users, players, transactions);
    private final User seller = new User(2L, "seller", "seller@test.com", "password");
    private final Player player = new Player(10L);
    private final Token first = new Token(101L, seller, player);
    private final Token second = new Token(102L, seller, player);
    private final Token remaining = new Token(103L, seller, player);

    @BeforeEach
    void setUp() {
        player.setCurrentPrice(20.0);
        player.setTokens(new ArrayList<>(List.of(first, second, remaining)));
        seller.setTokens(new ArrayList<>(List.of(first, second, remaining)));
        when(players.recuperar(10L)).thenReturn(Optional.of(player));
        when(users.recuperar(2L)).thenReturn(Optional.of(seller));
    }

    @Test
    void saleChecksAllTokenIdsOnceAndKeepsOriginalPersistenceFlow() {
        when(orders.saveAll(anyList())).thenAnswer(call -> call.getArgument(0));

        var result = market.sell(new PeticionVenta(seller, player, 2));

        assertEquals(List.of(first, second), result.stream().map(o -> o.getToken()).toList());
        assertEquals(List.of(remaining), seller.getTokens());
        assertNull(first.getOwner());
        assertNull(second.getOwner());
        assertSame(seller, remaining.getOwner());
        verify(orders).findFirstTokenIdWithOpenOrder(List.of(101L, 102L));
        verify(orders).saveAll(result);
        verifyNoMoreInteractions(orders);
        verify(users).guardar(seller);
        verify(players).guardar(player);
        verifyNoInteractions(transactions);
    }

    @Test
    void openOrderKeepsSpecificErrorAndPreventsSaving() {
        when(orders.findFirstTokenIdWithOpenOrder(List.of(101L, 102L))).thenReturn(Optional.of(102L));

        var error = assertThrows(ExisteOrdenAbiertaParaTokenException.class,
                () -> market.sell(new PeticionVenta(seller, player, 2)));

        assertEquals("Ya existe una orden abierta para el token: 102", error.getMessage());
        verify(orders).findFirstTokenIdWithOpenOrder(List.of(101L, 102L));
        verifyNoMoreInteractions(orders);
        verify(users, never()).guardar(any());
        verify(players, never()).guardar(any());
    }

    @Test
    void insufficientTokensDoNotCheckOrdersOrSave() {
        assertThrows(TokensInsuficientesParaVenderException.class,
                () -> market.sell(new PeticionVenta(seller, player, 4)));
        assertEquals(List.of(first, second, remaining), seller.getTokens());
        verifyNoInteractions(orders);
        verify(users, never()).guardar(any());
        verify(players, never()).guardar(any());
    }
}
