package com.overcode.model;

import com.overcode.model.exception.InvalidOrderQuantityException;
import com.overcode.model.player.Player;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class BuyOrder extends Order {

    private Integer quantity;

    public BuyOrder() {
        super();
    }

    public BuyOrder(User seller, Player player, Integer quantity) {
        super(null, seller, player, Instant.now());
        if (quantity == null || quantity <= 0) {
            throw new InvalidOrderQuantityException("Quantity must be greater than zero.");
        }
        this.quantity = quantity;
    }
}
