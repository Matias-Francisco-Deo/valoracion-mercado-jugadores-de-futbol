package com.overcode.controller.dto.user;

import com.overcode.controller.dto.market.DepositPetition;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DepositRequest(
        @NotNull(message = "Cantidad no puede ser nula")
        @Positive(message = "Cantidad debe ser mayor que cero")
        Double amount
) {

    public DepositPetition aModelo(Long userId) {
        return new DepositPetition(userId, amount);
    }

}