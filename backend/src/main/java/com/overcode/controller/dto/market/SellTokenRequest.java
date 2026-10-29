package com.overcode.controller.dto.market;

import com.overcode.model.venta.SellPost;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SellTokenRequest(
                @NotNull(message = "Player ID must not be null")
                Long playerId,

                @NotNull(message = "Quantity must not be null")
                @Positive(message = "Quantity must be greater than zero")
                Integer quantity)
    {

    public SellPost aModelo(Long userId) {
        return new SellPost(userId, this.playerId, this.quantity);
    }
}
