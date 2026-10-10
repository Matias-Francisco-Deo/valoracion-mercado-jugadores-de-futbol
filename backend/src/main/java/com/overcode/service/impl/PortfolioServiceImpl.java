package com.overcode.service.impl;

import com.overcode.model.Portfolio;
import com.overcode.persistence.repository.interfaces.PortfolioRepository;
import com.overcode.service.interfaces.PortfolioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;

    public PortfolioServiceImpl(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    @Override
    public Portfolio createForUser(Long userId) {
        return portfolioRepository.findByUserId(userId)
                .orElseGet(() -> portfolioRepository.save(
                        // null version marks a not-yet-persisted entity so Spring Data issues an INSERT
                        new Portfolio(UUID.randomUUID(), userId, BigDecimal.ZERO, null)));
    }

    @Override
    public Portfolio deposit(Long userId, BigDecimal amount) {
        // Get-or-create: a logged-in user without a portfolio yet (e.g. registered before this
        // feature existed) still gets funded instead of hitting a spurious "not found".
        Portfolio portfolio = createForUser(userId);
        // addCredits rejects null or non-positive amounts with IllegalArgumentException
        portfolio.addCredits(amount);
        return portfolioRepository.save(portfolio);
    }

    @Override
    @Transactional(readOnly = true)
    public Portfolio getByUserId(Long userId) {
        return portfolioRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Portfolio not found"));
    }
}
