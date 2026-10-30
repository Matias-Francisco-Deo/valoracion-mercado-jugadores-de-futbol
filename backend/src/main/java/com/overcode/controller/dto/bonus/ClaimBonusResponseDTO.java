package com.overcode.controller.dto.bonus;

import com.overcode.model.bonus.BonusClaim;

public record ClaimBonusResponseDTO(
    Double givenBonus,
    Integer currentStreak,
    String message
) {
    public static ClaimBonusResponseDTO desdeModelo(BonusClaim claim) {
        return new ClaimBonusResponseDTO(
                claim.givenBonus(),
                claim.currentStreak(),
                "¡Obtuviste la recompensa diaria!"
        );
    }
}
