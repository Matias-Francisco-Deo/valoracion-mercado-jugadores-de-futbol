package com.overcode.controller.dto.user;

import com.overcode.controller.dto.token.TokenPageResponseDTO;

public record PortfolioResponseDTO(
        TokenPageResponseDTO availableTokens,
        TokenPageResponseDTO sellingTokens
) {
    public static PortfolioResponseDTO desdeModelo(PortfolioDataDTO portfolioData){
        return new PortfolioResponseDTO(
                TokenPageResponseDTO.desdeModelo(portfolioData.availableTokens()),
                TokenPageResponseDTO.desdeModelo(portfolioData.sellingTokens())
        );
    }
}
