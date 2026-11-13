package com.overcode.persistence.repository;

import com.overcode.model.market.orden.OrderStatus;
import com.overcode.persistence.dto.jpa.*;
import com.overcode.persistence.repository.dao.jpa.SellOrderDAOJPA;
import com.overcode.persistence.repository.impl.OrdenDeCompraRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MarketOrderMappingTest {
    @Test
    void buyRequestsOnlyNeededOrdersAndSharesSellerInstance() {
        SellOrderDAOJPA dao = mock(SellOrderDAOJPA.class);
        UserJPADTO seller = new UserJPADTO(2L, "seller", "seller@test.com", "pass", 10.0, List.of());
        PlayerJPADTO player = new PlayerJPADTO(10L, "Player", 20.0, List.of());
        var first = new OrdenDeVentaJPADTO(1L, seller, player, new TokenJPADTO(101L, null, player),
                20.0, OrderStatus.OPEN, Instant.now());
        var second = new OrdenDeVentaJPADTO(2L, seller, player, new TokenJPADTO(102L, null, player),
                20.0, OrderStatus.OPEN, Instant.now());
        when(dao.findByPlayer_IdAndStatusAndOwner_IdNotOrderByPriceAscCreatedAtAsc(
                10L, OrderStatus.OPEN, 1L, PageRequest.of(0, 2))).thenReturn(List.of(first, second));

        var selected = new OrdenDeCompraRepositoryImpl(dao).findOpenSellOrders(10L, 1L, 2);

        assertEquals(List.of(1L, 2L), selected.stream().map(o -> o.getId()).toList());
        assertSame(selected.getFirst().getOwner(), selected.getLast().getOwner());
        verify(dao).findByPlayer_IdAndStatusAndOwner_IdNotOrderByPriceAscCreatedAtAsc(
                10L, OrderStatus.OPEN, 1L, PageRequest.of(0, 2));
        verifyNoMoreInteractions(dao);
    }

    @Test
    void conflictCheckUsesOneQueryForAllSelectedTokens() {
        SellOrderDAOJPA dao = mock(SellOrderDAOJPA.class);
        when(dao.findFirstTokenIdWithStatus(List.of(101L, 102L), OrderStatus.OPEN)).thenReturn(102L);
        assertEquals(Optional.of(102L), new OrdenDeCompraRepositoryImpl(dao)
                .findFirstTokenIdWithOpenOrder(List.of(101L, 102L)));
        verify(dao).findFirstTokenIdWithStatus(List.of(101L, 102L), OrderStatus.OPEN);
        verifyNoMoreInteractions(dao);
    }

    @Test
    void emptyConflictCheckDoesNotQueryDatabase() {
        SellOrderDAOJPA dao = mock(SellOrderDAOJPA.class);
        assertTrue(new OrdenDeCompraRepositoryImpl(dao).findFirstTokenIdWithOpenOrder(List.of()).isEmpty());
        verifyNoInteractions(dao);
    }
}
