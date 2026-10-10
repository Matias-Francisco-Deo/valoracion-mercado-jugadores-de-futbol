package com.overcode.service;

import com.overcode.model.Portfolio;
import com.overcode.model.Player;
import com.overcode.model.User;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import com.overcode.persistence.repository.interfaces.PortfolioRepository;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.service.interfaces.TokenMarketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@TestPropertySource(properties = "superuser.email=test-admin@gmail.com")
public class BuyTokensTest {

    @Autowired
    private TokenMarketService tokenMarketService;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    public void testBuy() {
        // Fetch or Create Super Admin
        User superAdmin = userRepository.findByEmail("test-admin@gmail.com").orElseGet(() -> {
            User admin = new User("admin-" + UUID.randomUUID(), "test-admin@gmail.com", "pass");
            return userRepository.guardar(admin);
        });
        
        Portfolio adminPortfolio = portfolioRepository.findByUserId(superAdmin.getId()).orElseGet(() -> {
            Portfolio p = new Portfolio(UUID.randomUUID(), superAdmin.getId(), BigDecimal.ZERO, null);
            return portfolioRepository.save(p);
        });
        
        // Create Player
        Player newPlayer = new Player();
        newPlayer.setName("Test Player");
        newPlayer.setCurrentPrice(10);
        final Player player = playerRepository.guardar(newPlayer);
        
        // Give tokens to admin so they can sell
        com.overcode.model.TokenHolding holding = new com.overcode.model.TokenHolding(UUID.randomUUID(), adminPortfolio.getId(), player.getId(), 100);
        adminPortfolio.addTokenHolding(holding);
        portfolioRepository.save(adminPortfolio);

        // Create Buyer
        User buyer = new User("buyer-" + UUID.randomUUID(), UUID.randomUUID() + "@gmail.com", "pass");
        buyer = userRepository.guardar(buyer);
        
        Portfolio buyerPortfolio = new Portfolio(UUID.randomUUID(), buyer.getId(), new BigDecimal("1000.00"), null);
        portfolioRepository.save(buyerPortfolio);

        // Execute Buy
        tokenMarketService.buyTokens(buyer.getId(), player.getId(), 5);

        // Check if buyer has the tokens
        var p = portfolioRepository.findByUserId(buyer.getId()).orElseThrow();
        boolean hasTokens = p.getTokenHoldings().stream()
            .anyMatch(h -> h.getPlayerId().equals(player.getId()) && h.getQuantity() == 5);
        assertTrue(hasTokens);
    }
}
