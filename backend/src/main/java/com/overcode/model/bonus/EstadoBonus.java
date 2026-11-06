package com.overcode.model.bonus;

import com.overcode.model.bonus.mission.Mission;

import java.util.ArrayList;
import java.util.List;

public record EstadoBonus(
        Integer currentStreak,
        Double nextBonus,
        Boolean alreadyClaimedToday,
        DiaryBonusRewards weeklyBonuses,
        Boolean puedeReclamar,
        List<Mission<?>> misiones
) {
    public EstadoBonus(Integer currentStreak, Double nextBonus, Boolean alreadyClaimedToday, DiaryBonusRewards weeklyBonuses) {
        this(currentStreak, nextBonus, alreadyClaimedToday, weeklyBonuses, !alreadyClaimedToday, new ArrayList<>());
    }
}
