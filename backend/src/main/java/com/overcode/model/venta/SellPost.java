package com.overcode.model.venta;

import com.overcode.model.exception.ParametroNoValidoException;

public record SellPost(Long userId, Long playerId, int quantity) {
    public SellPost {
        if (quantity <= 0) {
            throw new ParametroNoValidoException("La cantidad debe ser positiva");
        }
    }
}
