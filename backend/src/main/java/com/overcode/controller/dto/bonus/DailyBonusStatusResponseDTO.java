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
        List<MissionStatusResponseDTO> missionDTOs = estadoBonus.mision().getSubmissions().stream().map(MissionStatusResponseDTO::desdeModelo).toList();

        return new DailyBonusStatusResponseDTO(
                estadoBonus.currentStreak(),
                estadoBonus.nextBonus(),
                estadoBonus.alreadyClaimedToday(),
                estadoBonus.weeklyBonuses() != null ? estadoBonus.weeklyBonuses().getRewards() : List.of(),
                estadoBonus.canClaimBonus(),
                missionDTOs
        );
    }
}
