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
    public Instant getCreatedAt() { return createdAt; }
    public Long getVersion() { return version; }

    /** Whether this order is still resting in the book and available for matching. */
    public boolean isOpen() {
        return status == OrderStatus.OPEN;
    }

    /** True when this order was placed by the given user (used to prevent self-trading). */
    public boolean belongsTo(Long candidateUserId) {
        return this.userId.equals(candidateUserId);
    }

    /** Total escrow value of the order: price per token times the resting quantity. */
    public BigDecimal totalValue() {
        return pricePerToken.multiply(BigDecimal.valueOf(quantity));
    }

    /** Transition an OPEN order to COMPLETED once it has been fully filled. */
    public void markCompleted() {
        if (status != OrderStatus.OPEN) {
            throw new IllegalStateException("Only OPEN orders can be completed.");
        }
        this.status = OrderStatus.COMPLETED;
    }

    /** Transition an OPEN order to CANCELLED. */
    public void cancel() {
        if (status != OrderStatus.OPEN) {
            throw new IllegalStateException("Only OPEN orders can be cancelled.");
        }
        this.status = OrderStatus.CANCELLED;
    }

    /**
     * Consume part (or all) of the resting quantity when a taker fills against this order.
     * When the remaining quantity reaches zero the order is marked COMPLETED.
     */
    public void reduceQuantity(int taken) {
        if (taken <= 0) {
            throw new IllegalArgumentException("Taken quantity must be positive.");
        }
        if (taken > quantity) {
            throw new IllegalArgumentException("Cannot take more than the resting quantity.");
        }
        this.quantity -= taken;
        if (this.quantity == 0) {
            markCompleted();
        }
    }
}
