package com.overcode.model.market.orden;

import com.overcode.model.exception.InvalidOrderQuantityException;
import com.overcode.model.player.Player;
import com.overcode.model.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class OrdenDeCompra extends Orden {

    private Integer quantity;

    public OrdenDeCompra() {
        super();
    }

    public OrdenDeCompra(User seller, Player player, Integer quantity) {
        super(null, seller, player, Instant.now());
        if (quantity == null || quantity <= 0) {
            throw new InvalidOrderQuantityException("Cantidad debe ser mayor a cero");
        }
        this.quantity = quantity;
    }
}
