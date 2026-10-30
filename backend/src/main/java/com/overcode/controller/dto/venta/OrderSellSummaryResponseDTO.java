package com.overcode.controller.dto.venta;

import com.overcode.model.venta.orden.ResumenOrdenesDeVenta;

public record OrderSellSummaryResponseDTO(
        Long playerId,
        String playerName,
        Integer quantity,
        Double unitPrice
) {
    public static OrderSellSummaryResponseDTO desdeModelo(ResumenOrdenesDeVenta dto) {
        return new OrderSellSummaryResponseDTO(
                dto.playerId(),
                dto.playerName(),
                dto.quantity(),
                dto.unitPrice()
        );
    }
}
