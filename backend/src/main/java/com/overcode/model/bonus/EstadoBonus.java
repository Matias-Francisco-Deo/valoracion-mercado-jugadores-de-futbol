package com.overcode.model.bonus;

import com.overcode.model.bonus.mission.DailyBonusMission;

public record EstadoBonus(
        Integer currentStreak,
        Double nextBonus,
        Boolean alreadyClaimedToday,
        DailyBonusRewards weeklyBonuses,
        Boolean canClaimBonus,
        DailyBonusMission mision
) {
}
