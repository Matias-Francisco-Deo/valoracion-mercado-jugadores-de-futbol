package com.overcode.model.market;

import com.overcode.model.exception.ParametroNoValidoException;

public record PeticionVenta(Long userId, Long playerId, int quantity) {
    public PeticionVenta {
        if (quantity <= 0) {
            throw new ParametroNoValidoException("La amount debe ser positiva");
        }
    }
}
