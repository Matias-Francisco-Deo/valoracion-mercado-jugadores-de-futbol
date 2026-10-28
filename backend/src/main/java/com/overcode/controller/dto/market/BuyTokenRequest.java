package com.overcode.controller.dto.market;

import com.overcode.model.player.Player;
import com.overcode.model.user.User;
import com.overcode.model.venta.BuyOrder;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BuyTokenRequest (
    @NotNull(message = "Player ID must not be null")
    Long playerId,

    @NotNull(message = "Quantity must not be null")
    @Positive(message = "Quantity must be greater than zero")
    Integer quantity
) {

    public BuyOrder aModelo(Long userId) {
        User seller = new User();
        seller.setId(userId);
        seller.setUsername("placeholder");

        return new BuyOrder(
                seller,
                new Player(playerId),
                this.quantity);
    }
}
