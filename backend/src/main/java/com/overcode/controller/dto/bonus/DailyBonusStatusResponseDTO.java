package com.overcode.controller.dto.bonus;

import com.overcode.model.bonus.EstadoBonus;

import java.util.List;

public record DailyBonusStatusResponseDTO(
        Integer currentStreak,
        Double nextBonus,
        Boolean alreadyClaimedToday,
        List<Double> weeklyBonusRewards,
        Boolean puedeReclamar,
        List<MissionStatusResponseDTO> misiones
) {
    public static DailyBonusStatusResponseDTO desdeModelo(EstadoBonus estadoBonus) {
        List<MissionStatusResponseDTO> missionDTOs = estadoBonus.misiones() != null
                ? estadoBonus.misiones().stream().map(MissionStatusResponseDTO::desdeModelo).toList()
                : List.of();

        return new DailyBonusStatusResponseDTO(
                estadoBonus.currentStreak(),
                estadoBonus.nextBonus(),
                estadoBonus.alreadyClaimedToday(),
                estadoBonus.weeklyBonuses() != null ? estadoBonus.weeklyBonuses().getRewards() : List.of(),
                estadoBonus.puedeReclamar(),
                missionDTOs
        );
    }
}
