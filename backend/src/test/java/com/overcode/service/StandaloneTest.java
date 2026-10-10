package com.overcode.service;

import com.overcode.model.Portfolio;
import com.overcode.model.TokenHolding;
import com.overcode.persistence.dto.jpa.PortfolioJPADTO;
import com.overcode.persistence.dto.jpa.TokenHoldingJPADTO;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StandaloneTest {

    @Test
    public void testMapping() {
        Portfolio portfolio = new Portfolio(UUID.randomUUID(), 1L, BigDecimal.TEN, 1L);
        portfolio.addTokenHolding(new TokenHolding(UUID.randomUUID(), portfolio.getId(), 2L, 5));
        
        PortfolioJPADTO dto = PortfolioJPADTO.desdeModelo(portfolio);
        assertEquals(1, dto.getTokenHoldings().size());
        
        Portfolio mappedBack = dto.aModelo();
        assertEquals(1, mappedBack.getTokenHoldings().size());
        assertEquals(5, mappedBack.getTokenHoldings().get(0).getQuantity());
    }
}
