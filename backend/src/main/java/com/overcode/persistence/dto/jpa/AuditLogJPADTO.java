package com.overcode.persistence.dto.jpa;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_log")
public class AuditLogJPADTO {

    @Id
    private UUID id;

    @Column(name = "operation_type", nullable = false, updatable = false)
    private String operationType;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "counterparty_id", nullable = false, updatable = false)
    private Long counterpartyId;

    @Column(name = "player_id", nullable = false, updatable = false)
    private Long playerId;

    @Column(name = "token_amount", nullable = false, updatable = false)
    private int tokenAmount;

    @Column(name = "price_per_token", nullable = false, precision = 19, scale = 2, updatable = false)
    private BigDecimal pricePerToken;

    @Column(name = "timestamp", nullable = false, updatable = false)
    private Instant timestamp;

    protected AuditLogJPADTO() {}

    public AuditLogJPADTO(UUID id, String operationType, Long userId, Long counterpartyId,
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

    public UUID getId() { return id; }
    public String getOperationType() { return operationType; }
    public Long getUserId() { return userId; }
    public Long getCounterpartyId() { return counterpartyId; }
    public Long getPlayerId() { return playerId; }
    public int getTokenAmount() { return tokenAmount; }
    public BigDecimal getPricePerToken() { return pricePerToken; }
    public Instant getTimestamp() { return timestamp; }

    public com.overcode.model.AuditLog aModelo() {
        return new com.overcode.model.AuditLog(
                this.id,
                com.overcode.model.AuditLog.OperationType.valueOf(this.operationType),
                this.userId,
                this.counterpartyId,
                this.playerId,
                this.tokenAmount,
                this.pricePerToken,
                this.timestamp
        );
    }

    public static AuditLogJPADTO desdeModelo(com.overcode.model.AuditLog modelo) {
        return new AuditLogJPADTO(
                modelo.getId(),
                modelo.getOperationType().name(),
                modelo.getUserId(),
                modelo.getCounterpartyId(),
                modelo.getPlayerId(),
                modelo.getTokenAmount(),
                modelo.getPricePerToken(),
                modelo.getTimestamp()
        );
    }
}

