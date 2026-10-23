package com.overcode.controller.dto.bonus;

import com.overcode.model.bonus.BonusClaim;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClaimBonusResponseDTO {
    private Double creditsAwarded;
    private Integer newStreak;
    private String message;

    public static ClaimBonusResponseDTO desdeModelo(BonusClaim claim) {
        return new ClaimBonusResponseDTO(
                claim.givenBonus(),
                claim.currentStreak(),
                "¡Obtuviste la recompensa diaria!" // TODO mover?
        );
    }
}
