package com.overcode.service;

import com.overcode.model.Player;
import com.overcode.model.User;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.service.impl.TokenEmissionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TokenEmissionServiceImplUnitTest {

    private UserRepository userRepository;
    private TokenEmissionServiceImpl tokenEmissionService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        tokenEmissionService = new TokenEmissionServiceImpl(userRepository);
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

        tokenEmissionService.emitTokensForNewPlayers(List.of(player1, player2));

        verify(userRepository, times(1)).findByEmail("superadmin@test.com");
        verify(userRepository, times(1)).guardar(superadmin);
        assertEquals(100, superadmin.getTokenCountForPlayer(10L));
        assertEquals(100, superadmin.getTokenCountForPlayer(20L));
    }

    @Test
    void emitTokensForNewPlayersDoesNothingWhenListIsEmpty() {
        tokenEmissionService.emitTokensForNewPlayers(List.of());

        verifyNoInteractions(userRepository);
    }
}
