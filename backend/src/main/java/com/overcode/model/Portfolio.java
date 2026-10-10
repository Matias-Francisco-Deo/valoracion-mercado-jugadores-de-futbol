package com.overcode.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Portfolio {
    private UUID id;
    private Long userId;
    private BigDecimal credits;
    private List<TokenHolding> tokenHoldings;
    private Long version;

    public Portfolio(UUID id, Long userId, BigDecimal credits, Long version) {
        this.id = id;
        this.userId = userId;
        this.credits = credits != null ? credits : BigDecimal.ZERO;
        this.tokenHoldings = new ArrayList<>();
        // A null version marks a not-yet-persisted portfolio so Spring Data JPA issues an INSERT
        // (persist) instead of a merge, which would fail with StaleObjectStateException for a new row.
        this.version = version;
    }

    public UUID getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public BigDecimal getCredits() {
        return credits;
    }
    
    public Long getVersion() {
        return version;
    }

    public List<TokenHolding> getTokenHoldings() {
        return tokenHoldings;
    }

    public void addTokenHolding(TokenHolding holding) {
        this.tokenHoldings.add(holding);
    }

    public boolean hasSufficientCredits(BigDecimal amount) {
        return this.credits.compareTo(amount) >= 0;
    }

    public void addCredits(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.credits = this.credits.add(amount);
    }

    public void deductCredits(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (!hasSufficientCredits(amount)) {
            throw new IllegalStateException("Insufficient funds");
        }
        this.credits = this.credits.subtract(amount);
    }

    public Optional<TokenHolding> getHoldingForPlayer(Long playerId) {
        return tokenHoldings.stream()
                .filter(th -> th.getPlayerId().equals(playerId))
                .findFirst();
    }
    
    public TokenHolding getOrCreateHoldingForPlayer(Long playerId) {
        return getHoldingForPlayer(playerId).orElseGet(() -> {
            TokenHolding newHolding = new TokenHolding(UUID.randomUUID(), this.id, playerId, 0);
            this.tokenHoldings.add(newHolding);
            return newHolding;
        });
    }
}
