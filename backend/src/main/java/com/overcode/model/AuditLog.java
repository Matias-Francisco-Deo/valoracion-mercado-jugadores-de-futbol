package com.overcode.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class AuditLog {

    public enum OperationType {
        EMISSION,
        BUY,
        SELL
    }

    private final UUID id;
    private final OperationType operationType;
    private final Long userId;
    private final Long counterpartyId;
    private final Long playerId;
    private final int tokenAmount;
    private final BigDecimal pricePerToken;
    private final Instant timestamp;

    public AuditLog(UUID id, OperationType operationType, Long userId, Long counterpartyId,
                    Long playerId, int tokenAmount, BigDecimal pricePerToken, Instant timestamp) {
        this.id = id;
        this.operationType = operationType;
        this.userId = userId;
        this.counterpartyId = counterpartyId;
        this.playerId = playerId;
        this.tokenAmount = tokenAmount;
        this.pricePerToken = pricePerToken;
        this.timestamp = timestamp;
    }

    public UUID getId() {
        return id;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getCounterpartyId() {
        return counterpartyId;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public int getTokenAmount() {
        return tokenAmount;
    }

    public BigDecimal getPricePerToken() {
        return pricePerToken;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
