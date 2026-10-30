package com.overcode.controller.dto.user;

import com.overcode.model.market.PeticionDeposito;
import com.overcode.model.user.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DepositRequest(
        @NotNull(message = "Cantidad no puede ser nula")
        @Positive(message = "Cantidad debe ser mayor que cero")
        Double amount
) {

    public PeticionDeposito aModelo(Long userId) {
        return new PeticionDeposito(new User(userId), amount);
    }

}