package com.overcode.controller.dto.order;

public record OrderBuyRequest(
        Long playerId,
        Integer quantity
) {
}
