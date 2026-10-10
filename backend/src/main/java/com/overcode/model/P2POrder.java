package com.overcode.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class P2POrder {
    private UUID id;
    private Long userId;
    private Long playerId;
    private OrderType type;
    private int quantity;
    private BigDecimal pricePerToken;
    private OrderStatus status;
    private Instant createdAt;
    private Long version;

    public P2POrder(UUID id, Long userId, Long playerId, OrderType type, int quantity,
                    BigDecimal pricePerToken, OrderStatus status, Instant createdAt, Long version) {
        this.id = id;
        this.userId = userId;
        this.playerId = playerId;
        this.type = type;
        this.quantity = quantity;
        this.pricePerToken = pricePerToken;
        this.status = status;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.version = version;
    }

    public UUID getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getPlayerId() { return playerId; }
    public OrderType getType() { return type; }
    public int getQuantity() { return quantity; }
    public BigDecimal getPricePerToken() { return pricePerToken; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public Long getVersion() { return version; }
}
