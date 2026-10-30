package com.overcode.controller.dto.market;

import com.overcode.model.market.PeticionVenta;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SellTokenRequest(
                @NotNull(message = "Player ID must not be null")
                Long playerId,

                @NotNull(message = "Quantity must not be null")
                @Positive(message = "Quantity must be greater than zero")
                Integer quantity)
    {

    public PeticionVenta aModelo(Long userId) {
        return new PeticionVenta(userId, this.playerId, this.quantity);
    }
}
