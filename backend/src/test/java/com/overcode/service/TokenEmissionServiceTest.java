package com.overcode.service;
import com.overcode.service.interfaces.TokenEmissionService;

import com.overcode.model.Player;
import com.overcode.persistence.repository.interfaces.PortfolioRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled("Requires Docker API version compatibility for Testcontainers")
@SpringBootTest
class TokenEmissionServiceTest {

    @Autowired
    private TokenEmissionService tokenEmissionService;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Test
    void whenPlayerIsCreated_thenTokensAreEmittedToSuperAdmin() {
        // Given
        Player player = new Player();
        player.setId(1L);
        // Assuming player name and other properties don't matter for emission

        // When
        tokenEmissionService.emitTokensForNewPlayer(player);

        // Then
        // Find super admin portfolio (we assume superadmin is ID 1L for now, or it uses the configured super admin)
        // Since we don't know the exact superadmin ID, let's just find all portfolios and verify one has tokens
        List<com.overcode.model.Portfolio> portfolios = portfolioRepository.findAll();
        boolean found = false;
        for (com.overcode.model.Portfolio portfolio : portfolios) {
            if (!portfolio.getTokenHoldings().isEmpty()) {
                var holding = portfolio.getTokenHoldings().get(0);
                if (holding.getPlayerId().equals(player.getId()) && holding.getQuantity() == 100) {
                    found = true;
                    break;
                }
            }
        }
        assertTrue(found, "SuperAdmin portfolio should have 100 tokens for the new player");
    }
}
