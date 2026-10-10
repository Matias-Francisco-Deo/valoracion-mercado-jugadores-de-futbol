package com.overcode.service;

import com.overcode.service.interfaces.TokenMarketService;
import com.overcode.persistence.repository.interfaces.PortfolioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class BuyTokensTest {

    @Autowired
    private TokenMarketService tokenMarketService;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Test
    public void testBuy() {
        // User 743 is buyer, player 1247
        tokenMarketService.buyTokens(743L, 1247L, 5);

        // Check if 743 has the tokens
        var p = portfolioRepository.findByUserId(743L).get();
        boolean hasTokens = p.getTokenHoldings().stream().anyMatch(h -> h.getPlayerId() == 1247L && h.getQuantity() == 5);
        assertTrue(hasTokens);
    }
}
