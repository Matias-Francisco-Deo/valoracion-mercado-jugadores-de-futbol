package com.overcode.controller.dto.market;

import com.overcode.model.User;
import com.overcode.model.player.Player;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.ArrayList;

@Data
public class BuyTokenRequest {
    @NotNull(message = "Player ID must not be null")
    private Long playerId;

    @NotNull(message = "Quantity must not be null")
    @Positive(message = "Quantity must be greater than zero")
    private Integer quantity;

    public com.overcode.model.BuyOrder aModelo(Long userId) {
        return new com.overcode.model.BuyOrder(
                new User(userId, "placeholder", "placeholderEmail", "placeholder", 0d, new ArrayList<>()),
                new Player(playerId),
                this.quantity);
    }
}
