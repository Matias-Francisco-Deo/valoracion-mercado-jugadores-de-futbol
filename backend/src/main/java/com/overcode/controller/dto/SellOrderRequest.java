package com.overcode.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SellOrderRequest(
    @NotNull(message = "sellerId is required") Long sellerId,
    @NotNull(message = "buyerId is required") Long buyerId,
    @NotNull(message = "playerId is required") Long playerId,
    @NotNull(message = "quantity is required") @Positive(message = "quantity must be positive") Integer quantity
) {
}
