package com.overcode.persistence.dto.jpa;

import com.overcode.model.market.orden.OrdenDeVenta;
import com.overcode.model.market.orden.OrderStatus;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJPADTO owner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private PlayerJPADTO player;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "token_id", nullable = false)
    private TokenJPADTO token;

    @Column(name = "price", nullable = false)
    private Double price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 15)
    private OrderStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public SellOrderJPADTO(Long id, UserJPADTO owner, PlayerJPADTO player, TokenJPADTO token, Double price, OrderStatus status, Instant createdAt) {
        this.id = id;
        this.owner = owner;
        this.player = player;
        this.token = token;
        this.price = price;
        this.status = status;
        this.createdAt = createdAt;
    }

    public OrdenDeVenta aModelo() {
        return new OrdenDeVenta(id, owner.aModelo(), player.aModelo(), token.aModelo(), price, status, createdAt);
    }

    public static SellOrderJPADTO desdeModelo(OrdenDeVenta modelo) {
        if (modelo == null) {
            return null;
        }
        SellOrderJPADTO dto = new SellOrderJPADTO();
        dto.setId(modelo.getId());
        dto.setOwner(UserJPADTO.desdeModelo(modelo.getOwner()));
        dto.setPlayer(PlayerJPADTO.desdeModelo(modelo.getPlayer()));
        dto.setToken(TokenJPADTO.desdeModelo(modelo.getToken()));
        dto.setPrice(modelo.getPrice());
        dto.setStatus(modelo.getStatus());
        dto.setCreatedAt(modelo.getCreatedAt());
        return dto;
    }
}
