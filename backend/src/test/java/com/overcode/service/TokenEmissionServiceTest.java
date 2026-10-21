package com.overcode.service;

import com.overcode.model.player.Player;
import com.overcode.model.User;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.service.interfaces.TokenEmissionService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled("Requires Docker API version compatibility for Testcontainers")
@SpringBootTest
class TokenEmissionServiceTest {

    @Autowired
    private TokenEmissionService tokenEmissionService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void whenPlayerIsCreatedThenTokensAreEmittedToSuperAdmin() {
        Player player = new Player();
        player.setId(1L);

        tokenEmissionService.emitTokensForNewPlayer(player);

        User superadmin = userRepository.findByEmail("overcode@gmail.com").orElseThrow();
        assertEquals(100, superadmin.getTokenCountForPlayer(player.getId()),
                "SuperAdmin should hold 100 tokens for the new player");
    }
}
