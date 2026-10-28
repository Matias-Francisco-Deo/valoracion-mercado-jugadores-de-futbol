package com.overcode.controller.dto.p2p;

import com.overcode.model.venta.orden.OrderSellSummary;

public record OrderSellSummaryResponseDTO(
        Long playerId,
        String playerName,
        Integer quantity,
        Double unitPrice
) {
    public static OrderSellSummaryResponseDTO desdeModelo(OrderSellSummary dto) {
        return new OrderSellSummaryResponseDTO(
                dto.playerId(),
                dto.playerName(),
                dto.quantity(),
                dto.unitPrice()
        );
    }
}
