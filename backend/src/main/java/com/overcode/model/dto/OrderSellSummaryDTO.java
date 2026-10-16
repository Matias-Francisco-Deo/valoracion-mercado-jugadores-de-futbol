package com.overcode.model.dto;

public record OrderSellSummaryDTO(
        Long playerId,
        String playerName,
        Integer quantity,
        Double unitPrice
) {
}
