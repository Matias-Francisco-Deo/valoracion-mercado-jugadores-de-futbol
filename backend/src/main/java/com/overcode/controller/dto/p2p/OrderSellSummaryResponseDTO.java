package com.overcode.controller.dto.p2p;

import com.overcode.model.dto.OrderSellSummaryDTO;

public record OrderSellSummaryResponseDTO(
        Long playerId,
        String playerName,
        Integer quantity,
        Double unitPrice
) {
    public static OrderSellSummaryResponseDTO desdeModelo(OrderSellSummaryDTO dto) {
        return new OrderSellSummaryResponseDTO(
                dto.playerId(),
                dto.playerName(),
                dto.quantity(),
                dto.unitPrice()
        );
    }
}
