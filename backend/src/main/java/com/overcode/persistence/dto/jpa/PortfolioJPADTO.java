package com.overcode.persistence.dto.jpa;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "portfolio")
public class PortfolioJPADTO {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "credits", nullable = false, precision = 19, scale = 2)
    private BigDecimal credits;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TokenHoldingJPADTO> tokenHoldings = new ArrayList<>();

    protected PortfolioJPADTO() {}

    public PortfolioJPADTO(UUID id, Long userId, BigDecimal credits) {
        this.id = id;
        this.userId = userId;
        this.credits = credits;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public BigDecimal getCredits() { return credits; }
    public void setCredits(BigDecimal credits) { this.credits = credits; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public List<TokenHoldingJPADTO> getTokenHoldings() { return tokenHoldings; }
    public void setTokenHoldings(List<TokenHoldingJPADTO> tokenHoldings) {
        this.tokenHoldings.clear();
        if (tokenHoldings != null) {
            this.tokenHoldings.addAll(tokenHoldings);
            for (TokenHoldingJPADTO th : tokenHoldings) {
                th.setPortfolio(this);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioJPADTO that = (PortfolioJPADTO) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public com.overcode.model.Portfolio aModelo() {
        com.overcode.model.Portfolio portfolio = new com.overcode.model.Portfolio(
                this.id,
                this.userId,
                this.credits,
                this.version
        );
        if (this.tokenHoldings != null) {
            for (TokenHoldingJPADTO thEntity : this.tokenHoldings) {
                portfolio.addTokenHolding(thEntity.aModelo());
            }
        }
        return portfolio;
    }

    public static PortfolioJPADTO desdeModelo(com.overcode.model.Portfolio modelo) {
        PortfolioJPADTO entity = new PortfolioJPADTO();
        entity.setId(modelo.getId());
        entity.setUserId(modelo.getUserId());
        entity.setCredits(modelo.getCredits());
        entity.setVersion(modelo.getVersion());
        
        List<TokenHoldingJPADTO> holdings = new ArrayList<>();
        if (modelo.getTokenHoldings() != null) {
            for (com.overcode.model.TokenHolding holdingModelo : modelo.getTokenHoldings()) {
                holdings.add(TokenHoldingJPADTO.desdeModelo(holdingModelo, entity));
            }
        }
        entity.setTokenHoldings(holdings);
        return entity;
    }
}

