package com.overcode.service;

import com.overcode.model.player.Player;
import com.overcode.model.user.User;
import com.overcode.model.venta.OrdenDeCompra;
import com.overcode.model.venta.orden.OrderStatus;
import com.overcode.persistence.repository.interfaces.OrdenDeCompraRepository;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.service.impl.TokenEmissionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class TokenEmissionServiceImplUnitTest {

    private UserRepository userRepository;
    private TokenEmissionServiceImpl tokenEmissionService;
    private OrdenDeCompraRepository ordenDeCompraRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        ordenDeCompraRepository = mock(OrdenDeCompraRepository.class);
        tokenEmissionService = new TokenEmissionServiceImpl(userRepository, ordenDeCompraRepository);
        ReflectionTestUtils.setField(tokenEmissionService, "superuserEmail", "superuser@test.com");
    }

    @Test
    void emitTokensForNewPlayersEmitsTokensInBatch() {
        User superuser = new User("admin", "superuser@test.com", "pass");
        superuser.setId(1L);
        when(userRepository.findByEmail("superuser@test.com")).thenReturn(Optional.of(superuser));

        Player player1 = com.overcode.testUtils.TestPlayerUtil.getJugadorConNombre("Player 1");
        player1.setId(10L);
        Player player2 = com.overcode.testUtils.TestPlayerUtil.getJugadorConNombre("Player 2");
        player2.setId(20L);

        ArgumentCaptor<List<OrdenDeCompra>> ordersCaptor = ArgumentCaptor.forClass(List.class);

        tokenEmissionService.emitTokensForNewPlayers(List.of(player1, player2));

        verify(userRepository, times(1)).findByEmail("superuser@test.com");
        verify(userRepository, times(1)).guardar(superuser);
        verify(ordenDeCompraRepository, times(2)).saveAll(ordersCaptor.capture());

        List<List<OrdenDeCompra>> allOrders = ordersCaptor.getAllValues();
        assertEquals(2, allOrders.size());
        assertEquals(100, player1.getTokens().size());
        assertEquals(100, player2.getTokens().size());

        for (List<OrdenDeCompra> orders : allOrders) {
            assertEquals(100, orders.size());
            assertTrue(orders.stream().allMatch(OrdenDeCompra::isOpen));
            assertTrue(orders.stream().allMatch(order -> order.getStatus() == OrderStatus.OPEN));
        }
    }

    @Test
    void emitTokensForNewPlayersDoesNothingWhenListIsEmpty() {
        tokenEmissionService.emitTokensForNewPlayers(List.of());

        verifyNoInteractions(userRepository);
    }
}
