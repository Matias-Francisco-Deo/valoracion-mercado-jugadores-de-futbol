package com.overcode.controller.dto.portfolio;

import com.overcode.model.Portfolio;

import java.math.BigDecimal;
import java.util.List;

public class PortfolioResponseDTO {
    private final BigDecimal credits;
    private final List<TokenHoldingResponseDTO> holdings;

    public PortfolioResponseDTO(BigDecimal credits, List<TokenHoldingResponseDTO> holdings) {
        this.credits = credits;
        this.holdings = holdings;
    }

    public static PortfolioResponseDTO from(Portfolio portfolio) {
        List<TokenHoldingResponseDTO> holdings = portfolio.getTokenHoldings().stream()
                .map(TokenHoldingResponseDTO::from)
                .toList();
        return new PortfolioResponseDTO(portfolio.getCredits(), holdings);
    }

    public BigDecimal getCredits() { return credits; }
    public List<TokenHoldingResponseDTO> getHoldings() { return holdings; }
}
