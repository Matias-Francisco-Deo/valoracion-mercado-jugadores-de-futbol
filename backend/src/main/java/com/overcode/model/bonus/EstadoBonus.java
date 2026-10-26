package com.overcode.model.bonus;

public record EstadoBonus(Integer currentStreak,
                          Double nextBonus,
                          Boolean alreadyClaimedToday,
                          LoginBonusRewards weeklyBonuses) {
}
