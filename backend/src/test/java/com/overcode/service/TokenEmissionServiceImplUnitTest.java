package com.overcode.service;
import com.overcode.model.SellOrder;
import com.overcode.model.User;
import com.overcode.model.player.Player;
import com.overcode.model.orden.OrderStatus;
import com.overcode.persistence.repository.interfaces.SellOrderRepository;
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
    private SellOrderRepository sellOrderRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        sellOrderRepository = mock(SellOrderRepository.class);
        tokenEmissionService = new TokenEmissionServiceImpl(userRepository,sellOrderRepository);
        ReflectionTestUtils.setField(tokenEmissionService, "superadminEmail", "superadmin@test.com");
    }

    @Test
    void emitTokensForNewPlayersEmitsTokensInBatch() {
        User superadmin = new User("admin", "superadmin@test.com", "pass");
        superadmin.setId(1L);
        when(userRepository.findByEmail("superadmin@test.com")).thenReturn(Optional.of(superadmin));

        Player player1 = com.overcode.testUtils.TestPlayerUtil.getJugadorConNombre("Player 1");
        player1.setId(10L);
        Player player2 = com.overcode.testUtils.TestPlayerUtil.getJugadorConNombre("Player 2");
        player2.setId(20L);

        ArgumentCaptor<List<SellOrder>> ordersCaptor = ArgumentCaptor.forClass(List.class);

        tokenEmissionService.emitTokensForNewPlayers(List.of(player1, player2));

        verify(userRepository, times(1)).findByEmail("superadmin@test.com");
        verify(userRepository, times(1)).guardar(superadmin);
        verify(sellOrderRepository, times(2)).saveAll(ordersCaptor.capture());

        List<List<SellOrder>> allOrders = ordersCaptor.getAllValues();
        assertEquals(2, allOrders.size());
        assertEquals(100, player1.getTokens().size());
        assertEquals(100, player2.getTokens().size());

        for (List<SellOrder> orders : allOrders) {
            assertEquals(100, orders.size());
            assertTrue(orders.stream().allMatch(SellOrder::isOpen));
            assertTrue(orders.stream().allMatch(order -> order.getStatus() == OrderStatus.OPEN));
        }
    }

    @Test
    void emitTokensForNewPlayersDoesNothingWhenListIsEmpty() {
        tokenEmissionService.emitTokensForNewPlayers(List.of());

        verifyNoInteractions(userRepository);
    }
}
