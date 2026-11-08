package com.overcode.model.bonus;

import com.overcode.model.bonus.mission.Mission;

public record EstadoBonus(
        Integer currentStreak,
        Double nextBonus,
        Boolean alreadyClaimedToday,
        DiaryBonusRewards weeklyBonuses,
        Boolean puedeReclamar,
        Mission<?> mision
) {
//    public EstadoBonus(Integer currentStreak, Double nextBonus, Boolean alreadyClaimedToday, DiaryBonusRewards weeklyBonuses, Boolean puedeReclamar, Mission<?> mision) {
//        this(currentStreak, nextBonus, alreadyClaimedToday, weeklyBonuses, puedeReclamar, mision);
//    }
}
