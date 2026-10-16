package com.overcode.model;

import com.overcode.model.orden.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class SellOrder extends Order {

    private Long tokenId;
    private Integer price;
    private OrderStatus status;

    public SellOrder() {
        super();
        this.status = OrderStatus.OPEN;
    }

    public SellOrder(Long id, Long sellerId, Long playerId, Long tokenId, Integer price, OrderStatus status, Instant createdAt) {
        super(id, sellerId, playerId, createdAt);
        this.tokenId = tokenId;
        this.price = price;
        this.status = status != null ? status : OrderStatus.OPEN;
    }

    public SellOrder(Long sellerId, Long playerId, Long tokenId, Integer price) {
        this(null, sellerId, playerId, tokenId, price, OrderStatus.OPEN, Instant.now());
    }

    public boolean isOpen() {
        return this.status == OrderStatus.OPEN;
    }

    public boolean belongsTo(Long candidateUserId) {
        return this.userId != null && this.userId.equals(candidateUserId);
    }

    public void markCompleted() {
        if (!isOpen()) {
            throw new IllegalStateException("Only OPEN orders can be completed.");
        }
        this.status = OrderStatus.COMPLETED;
    }

    public void cancel() {
        if (!isOpen()) {
            throw new IllegalStateException("Only OPEN orders can be cancelled.");
        }
        this.status = OrderStatus.CANCELLED;
    }
}
