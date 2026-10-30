package com.overcode.controller.dto.portfolio;

import com.overcode.model.venta.Position;

public record PositionResponseDTO(
        Long playerId,
        String playerName,
        Integer quantity,
        Double averagePurchasePrice,
        Double currentPrice,
        Double currentValue,
        Double profitLoss,
        Double profitLossPercentage
) {
    public static PositionResponseDTO desdeModelo(Position position){
        return new PositionResponseDTO(
                position.getPlayer().getId(),
                position.getPlayer().getName(),
                position.getQuantity(),
                position.getAveragePurchasePrice(),
                position.getCurrentPrice(),
                position.getCurrentValue(),
                position.getProfitLoss(),
                position.getProfitLossPercentage()
        );
    }
}
