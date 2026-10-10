package com.overcode.model;

import java.util.UUID;

public class TokenHolding {
    private UUID id;
    private UUID portfolioId;
    private Long playerId;
    private int quantity;

    public TokenHolding(UUID id, UUID portfolioId, Long playerId, int quantity) {
        this.id = id;
        this.portfolioId = portfolioId;
        this.playerId = playerId;
        this.quantity = quantity;
    }

    public UUID getId() {
        return id;
    }

    public UUID getPortfolioId() {
        return portfolioId;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean hasSufficientTokens(int amount) {
        return this.quantity >= amount;
    }

    public void addTokens(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.quantity += amount;
    }

    public void deductTokens(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (!hasSufficientTokens(amount)) {
            throw new IllegalStateException("Insufficient tokens");
        }
        this.quantity -= amount;
    }
}
