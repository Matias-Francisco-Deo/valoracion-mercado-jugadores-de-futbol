package com.overcode.persistence.dto.jpa;

import com.overcode.model.OrderStatus;
import com.overcode.model.OrderType;
import com.overcode.model.P2POrder;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "p2p_order", indexes = {
    @Index(name = "idx_p2porder_match", columnList = "player_id, status, type, price_per_token, created_at")
})
public class P2POrderJPADTO {
    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "player_id", nullable = false)
    private Long playerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private OrderType type;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price_per_token", nullable = false, precision = 19, scale = 2)
    private BigDecimal pricePerToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 15)
    private OrderStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Version
    @Column(name = "version")
    private Long version;

    protected P2POrderJPADTO() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }
    public OrderType getType() { return type; }
    public void setType(OrderType type) { this.type = type; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getPricePerToken() { return pricePerToken; }
    public void setPricePerToken(BigDecimal pricePerToken) { this.pricePerToken = pricePerToken; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public P2POrder aModelo() {
        return new P2POrder(id, userId, playerId, type, quantity, pricePerToken, status, createdAt, version);
    }

    public static P2POrderJPADTO desdeModelo(P2POrder modelo) {
        P2POrderJPADTO dto = new P2POrderJPADTO();
        dto.setId(modelo.getId());
        dto.setUserId(modelo.getUserId());
        dto.setPlayerId(modelo.getPlayerId());
        dto.setType(modelo.getType());
        dto.setQuantity(modelo.getQuantity());
        dto.setPricePerToken(modelo.getPricePerToken());
        dto.setStatus(modelo.getStatus());
        dto.setCreatedAt(modelo.getCreatedAt());
        dto.setVersion(modelo.getVersion());
        return dto;
    }
}
