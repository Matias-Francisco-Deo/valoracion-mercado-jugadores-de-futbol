package com.overcode.controller.dto.market;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class BuyTokenRequest {
    @NotNull(message = "Player ID must not be null")
    private Long playerId;

    @NotNull(message = "Quantity must not be null")
    @Positive(message = "Quantity must be greater than zero")
    private Integer quantity;
}
