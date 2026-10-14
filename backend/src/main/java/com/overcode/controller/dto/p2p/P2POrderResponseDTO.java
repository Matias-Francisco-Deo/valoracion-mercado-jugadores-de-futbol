package com.overcode.controller.dto.p2p;

import com.overcode.model.OrderStatus;
import com.overcode.model.OrderType;
import com.overcode.model.P2POrder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class P2POrderResponseDTO {
    private final UUID id;
    private final Long userId;
    private final Long playerId;
    private final OrderType type;
    private final int quantity;
    private final BigDecimal pricePerToken;
    private final OrderStatus status;
    private final Instant createdAt;

    public P2POrderResponseDTO(P2POrder order) {
        this.id = order.getId();
        this.userId = order.getUserId();
        this.playerId = order.getPlayerId();
        this.type = order.getType();
        this.quantity = order.getQuantity();
        this.pricePerToken = order.getPricePerToken();
        this.status = order.getStatus();
        this.createdAt = order.getCreatedAt();
    }

    public static P2POrderResponseDTO from(P2POrder order) {
        return new P2POrderResponseDTO(order);
    }

    public UUID getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getPlayerId() { return playerId; }
    public OrderType getType() { return type; }
    public int getQuantity() { return quantity; }
    public BigDecimal getPricePerToken() { return pricePerToken; }
    public OrderStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
