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
    private BigDecimal pricePerToken; // TODO no bigdecimal
    private OrderStatus status;
    private Instant createdAt; // TODO LocalDate?
    private Long version;

    private P2POrder(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.playerId = builder.playerId;
        this.type = builder.type;
        this.quantity = builder.quantity;
        this.pricePerToken = builder.pricePerToken;
        this.status = builder.status;
        this.createdAt = builder.createdAt != null ? builder.createdAt : Instant.now();
        this.version = builder.version;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private Long userId;
        private Long playerId;
        private OrderType type;
        private int quantity;
        private BigDecimal pricePerToken;
        private OrderStatus status;
        private Instant createdAt;
        private Long version;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder playerId(Long playerId) {
            this.playerId = playerId;
            return this;
        }

        public Builder type(OrderType type) {
            this.type = type;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder pricePerToken(BigDecimal pricePerToken) {
            this.pricePerToken = pricePerToken;
            return this;
        }

        public Builder status(OrderStatus status) {
            this.status = status;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder version(Long version) {
            this.version = version;
            return this;
        }

        public P2POrder build() {
            return new P2POrder(this);
        }
    }

    public UUID getId() { return id; } // TODO usá lombok para los getters y los setters
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
