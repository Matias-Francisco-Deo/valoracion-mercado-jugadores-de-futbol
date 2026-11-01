package com.overcode.model.market.orden;

public record ResumenOrdenesDeVenta(
        Long playerId,
        String playerName,
        Integer quantity,
        Double unitPrice
) {
}
