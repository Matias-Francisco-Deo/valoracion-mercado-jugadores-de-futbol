package com.overcode.controller.dto.bonus;

import com.overcode.model.bonus.EstadoBonus;

import java.util.List;

public record DailyBonusStatusResponseDTO(Integer currentStreak, Double nextBonus, Boolean alreadyClaimedToday, List<Double> weeklyBonusRewards) {

    public static DailyBonusStatusResponseDTO desdeModelo(EstadoBonus estadoBonus) {
        return new DailyBonusStatusResponseDTO(
            estadoBonus.currentStreak(),
            estadoBonus.nextBonus(),
            estadoBonus.alreadyClaimedToday(),
            estadoBonus.weeklyBonuses().getRewards()
        );
    }
}
