package com.overcode.model.bonus;

public record BonusClaim(
    Double givenBonus,
    Integer currentStreak,
    Double currentCreditBalance
) {
}
