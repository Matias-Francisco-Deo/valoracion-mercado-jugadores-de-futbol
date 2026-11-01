package com.overcode.model.market;

import com.overcode.model.exception.ParametroNoValidoException;
import com.overcode.model.player.Player;
import com.overcode.model.user.User;

public record PeticionCancelacion(User user, Player player, int quantity) {
    public PeticionCancelacion {
        if (quantity <= 0) {
            throw new ParametroNoValidoException("La cantidad debe ser positiva");
        }
    }
}

