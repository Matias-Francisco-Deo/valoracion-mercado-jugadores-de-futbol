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

    public P2POrderResponseDTO(UUID id, Long userId, Long playerId, OrderType type, int quantity,
                                BigDecimal pricePerToken, OrderStatus status, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.playerId = playerId;
        this.type = type;
        this.quantity = quantity;
        this.pricePerToken = pricePerToken;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static P2POrderResponseDTO from(P2POrder order) {
        return new P2POrderResponseDTO(order.getId(), order.getUserId(), order.getPlayerId(), order.getType(),
                order.getQuantity(), order.getPricePerToken(), order.getStatus(), order.getCreatedAt());
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
