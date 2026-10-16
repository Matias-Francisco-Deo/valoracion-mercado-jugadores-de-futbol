package com.overcode.persistence.dto.jpa;

import com.overcode.model.orden.OrderStatus;
import com.overcode.model.SellOrder;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "sell_orders", indexes = {
    @Index(name = "idx_sellorder_match", columnList = "player_id, status, price, created_at")
})
@Getter
@Setter
@NoArgsConstructor
public class SellOrderJPADTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "player_id", nullable = false)
    private Long playerId;

    @Column(name = "token_id", nullable = false)
    private Long tokenId;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 15)
    private OrderStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public SellOrderJPADTO(Long id, Long userId, Long playerId, Long tokenId, Integer price, OrderStatus status, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.playerId = playerId;
        this.tokenId = tokenId;
        this.price = price;
        this.status = status;
        this.createdAt = createdAt;
    }

    public SellOrder aModelo() {
        return new SellOrder(id, userId, playerId, tokenId, price, status, createdAt);
    }

    public static SellOrderJPADTO desdeModelo(SellOrder modelo) {
        if (modelo == null) {
            return null;
        }
        SellOrderJPADTO dto = new SellOrderJPADTO();
        dto.setId(modelo.getId());
        dto.setUserId(modelo.getUserId());
        dto.setPlayerId(modelo.getPlayerId());
        dto.setTokenId(modelo.getTokenId());
        dto.setPrice(modelo.getPrice());
        dto.setStatus(modelo.getStatus());
        dto.setCreatedAt(modelo.getCreatedAt());
        return dto;
    }
}
