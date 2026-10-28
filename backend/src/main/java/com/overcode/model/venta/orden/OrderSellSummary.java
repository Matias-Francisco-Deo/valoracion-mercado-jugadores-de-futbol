package com.overcode.model.venta.orden;

public record OrderSellSummary(
        Long playerId,
        String playerName,
        Integer quantity,
        Double unitPrice
) {
}
