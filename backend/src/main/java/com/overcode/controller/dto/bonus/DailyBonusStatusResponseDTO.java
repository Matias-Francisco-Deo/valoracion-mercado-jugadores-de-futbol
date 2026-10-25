package com.overcode.controller.dto.bonus;

import com.overcode.model.bonus.EstadoBonus;

import java.util.List;

public record DailyBonusStatusResponseDTO(Integer currentStreak, Double todayBonus, Boolean alreadyClaimedToday, List<Double> weeklyBonuses) {

    public static DailyBonusStatusResponseDTO desdeModelo(EstadoBonus estadoBonus) {
        return new DailyBonusStatusResponseDTO(
            estadoBonus.currentStreak(),
            estadoBonus.todayBonus(),
            estadoBonus.alreadyClaimedToday(),
            estadoBonus.weeklyBonuses().getRewards()
        );
    }
}
