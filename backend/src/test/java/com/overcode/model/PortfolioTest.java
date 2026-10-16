package com.overcode.model;

import com.overcode.model.exception.InsufficientFundsException;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioTest {

    // TODO bigdecimal no

    @Test
    void testAddAndDeductCredits() {
        Portfolio portfolio = new Portfolio(UUID.randomUUID(), 1L, BigDecimal.valueOf(100), 0L);
        
        assertTrue(portfolio.hasSufficientCredits(BigDecimal.valueOf(50)));
        
        portfolio.deductCredits(BigDecimal.valueOf(50));
        assertEquals(0, BigDecimal.valueOf(50).compareTo(portfolio.getCredits()));
        
        BigDecimal amountOverBalance = BigDecimal.valueOf(60);
        assertThrows(InsufficientFundsException.class, () -> portfolio.deductCredits(amountOverBalance));
        
        portfolio.addCredits(BigDecimal.valueOf(10));
        assertEquals(0, BigDecimal.valueOf(60).compareTo(portfolio.getCredits()));
    }

    @Test
    void testAddAndDeductTokens() {
        Portfolio portfolio = new Portfolio(UUID.randomUUID(), 2L, BigDecimal.ZERO, 0L);
        TokenHolding holding = portfolio.getOrCreateHoldingForPlayer(1L);
        
        assertEquals(0, holding.getQuantity());
        
        holding.addTokens(10);
        assertTrue(holding.hasSufficientTokens(5));
        
        holding.deductTokens(5);
        assertEquals(5, holding.getQuantity());
        
        assertThrows(IllegalStateException.class, () -> holding.deductTokens(10));
    }
}
