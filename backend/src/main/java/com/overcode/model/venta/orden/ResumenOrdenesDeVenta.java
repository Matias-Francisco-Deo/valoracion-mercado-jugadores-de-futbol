package com.overcode.model.venta.orden;

public record ResumenOrdenesDeVenta(
        Long playerId,
        String playerName,
        Integer quantity,
        Double unitPrice
) {
}
