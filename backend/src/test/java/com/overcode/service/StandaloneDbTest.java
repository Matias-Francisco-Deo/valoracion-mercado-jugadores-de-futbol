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
public class StandaloneDbTest {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Test
    @Transactional
    public void testDbSave() {
        // Create user 999 portfolio
        Portfolio p = new Portfolio(UUID.randomUUID(), 999L, BigDecimal.TEN, null);
        portfolioRepository.save(p);
        
        // Add token holding
        Portfolio dbP = portfolioRepository.findByUserId(999L).get();
        TokenHolding th = dbP.getOrCreateHoldingForPlayer(555L);
        th.addTokens(10);
        portfolioRepository.save(dbP);

        // Fetch again
        Portfolio fetched = portfolioRepository.findByUserId(999L).get();
        assertEquals(1, fetched.getTokenHoldings().size());
        assertEquals(10, fetched.getTokenHoldings().get(0).getQuantity());
    }
}
