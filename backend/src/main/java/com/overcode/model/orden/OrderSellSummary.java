package com.overcode.model.orden;

public record OrderSellSummary(
        Long playerId,
        String playerName,
        Integer quantity,
        Double unitPrice
) {
}
