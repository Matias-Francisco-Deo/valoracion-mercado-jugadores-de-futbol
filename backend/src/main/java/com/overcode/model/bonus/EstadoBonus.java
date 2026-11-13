package com.overcode.model.bonus;

import com.overcode.model.bonus.mission.DailyBonusMission;

public record EstadoBonus(
        Integer currentStreak,
        Double nextBonus,
        Boolean alreadyClaimedToday,
        DailyBonusRewards weeklyBonuses,
        Boolean canClaimBonus,
        DailyBonusMission<?> mision
) {
//    public EstadoBonus(Integer currentStreak, Double nextBonus, Boolean alreadyClaimedToday, DiaryBonusRewards weeklyBonuses, Boolean canClaimBonus, Mission<?> mision) {
//        this(currentStreak, nextBonus, alreadyClaimedToday, weeklyBonuses, canClaimBonus, mision);
//    }
}
