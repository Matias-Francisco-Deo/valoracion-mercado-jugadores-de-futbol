package com.overcode.model.bonus;

import com.overcode.model.bonus.mission.DailyBonusMission;

public record EstadoBonus(
        Integer currentStreak,
        Double nextBonus,
        Boolean alreadyClaimedToday,
        DailyBonusRewards weeklyBonuses,
        Boolean puedeReclamar,
        DailyBonusMission<?> mision
) {
//    public EstadoBonus(Integer currentStreak, Double nextBonus, Boolean alreadyClaimedToday, DiaryBonusRewards weeklyBonuses, Boolean puedeReclamar, Mission<?> mision) {
//        this(currentStreak, nextBonus, alreadyClaimedToday, weeklyBonuses, puedeReclamar, mision);
//    }
}
