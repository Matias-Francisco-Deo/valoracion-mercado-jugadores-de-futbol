package com.overcode.service;

import com.overcode.model.Portfolio;
import com.overcode.model.TokenHolding;
import com.overcode.persistence.repository.interfaces.PortfolioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class StandaloneDbTest { // TODO test medio raaaaaaaaaaaaaaaaaro

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Test
    @Transactional
    void testDbSave() {
        long testUserId = Math.abs(UUID.randomUUID().getLeastSignificantBits());
        // Create user portfolio
        Portfolio p = new Portfolio(UUID.randomUUID(), testUserId, BigDecimal.TEN, null);
        portfolioRepository.save(p);
        
        // Add token holding
        Portfolio dbP = portfolioRepository.findByUserId(testUserId).get();
        TokenHolding th = dbP.getOrCreateHoldingForPlayer(555L);
        th.addTokens(10);
        portfolioRepository.save(dbP);

        // Fetch again
        Portfolio fetched = portfolioRepository.findByUserId(testUserId).get();
        assertEquals(1, fetched.getTokenHoldings().size());
        assertEquals(10, fetched.getTokenHoldings().get(0).getQuantity());
    }
}
