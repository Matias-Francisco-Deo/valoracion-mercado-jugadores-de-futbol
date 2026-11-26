package com.overcode.model.position;

public record PlayerPositionData(
        Long playerId,
        String playerName,
        Double currentPrice,
        Integer totalBought,
        Integer totalSold,
        Double totalPurchaseAmount
) {
}