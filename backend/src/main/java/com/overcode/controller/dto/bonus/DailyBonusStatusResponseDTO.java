package com.overcode.controller.dto.bonus;

import com.overcode.model.bonus.DiaDeLaSemana;
import com.overcode.model.bonus.EstadoBonus;

import java.util.Map;

public record DailyBonusStatusResponseDTO(Integer currentStreak, Double todayBonus, Boolean alreadyClaimedToday, Map<DiaDeLaSemana, Double> weeklyBonuses) {

    public static DailyBonusStatusResponseDTO desdeModelo(EstadoBonus estadoBonus) {
        return new DailyBonusStatusResponseDTO(
            estadoBonus.currentStreak(),
            estadoBonus.todayBonus(),
            estadoBonus.alreadyClaimedToday(),
            estadoBonus.weeklyBonuses()
        );
    }
}
