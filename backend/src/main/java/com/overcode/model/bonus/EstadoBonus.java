package com.overcode.model.bonus;

public record EstadoBonus(Integer currentStreak,
                          Double todayBonus,
                          Boolean alreadyClaimedToday,
                          LoginBonusRewards weeklyBonuses) {
}
