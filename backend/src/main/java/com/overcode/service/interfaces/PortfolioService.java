package com.overcode.service.interfaces;

import com.overcode.model.Portfolio;

import java.math.BigDecimal;

public interface PortfolioService {

    /**
     * Creates an empty portfolio (zero credits) for the given user.
     * Idempotent: returns the existing portfolio if one is already present.
     */
    Portfolio createForUser(Long userId);

    /**
     * Adds credits to the user's portfolio. The funding mechanism the token-market
     * feature left out of scope.
     */
    Portfolio deposit(Long userId, BigDecimal amount);

    Portfolio getByUserId(Long userId);
}
