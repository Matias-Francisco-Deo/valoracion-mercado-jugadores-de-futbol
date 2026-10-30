package com.overcode.controller.dto.portfolio;

import com.overcode.model.user.Portfolio;

import java.util.List;

public record PortfolioResponseDTO(
        List<PositionResponseDTO> positions
) {
    public static PortfolioResponseDTO desdeModelo(Portfolio portfolio){
        return new PortfolioResponseDTO(
                portfolio.getPositions().stream().map(PositionResponseDTO::desdeModelo).toList()
        );
    }
}

