package com.overcode.persistence.dto.jpa;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "token_holding", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"portfolio_id", "player_id"})
})
public class TokenHoldingJPADTO {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private PortfolioJPADTO portfolio;

    @Column(name = "player_id", nullable = false)
    private Long playerId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    protected TokenHoldingJPADTO() {}

    public TokenHoldingJPADTO(UUID id, PortfolioJPADTO portfolio, Long playerId, int quantity) {
        this.id = id;
        this.portfolio = portfolio;
        this.playerId = playerId;
        this.quantity = quantity;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public PortfolioJPADTO getPortfolio() { return portfolio; }
    public void setPortfolio(PortfolioJPADTO portfolio) { this.portfolio = portfolio; }

    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenHoldingJPADTO that = (TokenHoldingJPADTO) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public com.overcode.model.TokenHolding aModelo() {
        return new com.overcode.model.TokenHolding(
                this.id,
                this.portfolio.getId(),
                this.playerId,
                this.quantity
        );
    }

    public static TokenHoldingJPADTO desdeModelo(com.overcode.model.TokenHolding modelo, PortfolioJPADTO portfolioEntity) {
        return new TokenHoldingJPADTO(
                modelo.getId(),
                portfolioEntity,
                modelo.getPlayerId(),
                modelo.getQuantity()
        );
    }
}

