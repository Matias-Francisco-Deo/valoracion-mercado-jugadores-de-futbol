package com.overcode.controller.dto.market;

import com.overcode.model.market.PeticionCancelacion;
import com.overcode.model.player.Player;
import com.overcode.model.user.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CancellTokenRequest(
        @NotNull(message = "Player ID must not be null")
        Long playerId,

        @NotNull(message = "Quantity must not be null")
        @Positive(message = "Quantity must be greater than zero")
        Integer quantity)
{

    public PeticionCancelacion aModelo(Long userId) {
        return new PeticionCancelacion(new User(userId), new Player(playerId), this.quantity);
    }
}
