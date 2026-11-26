package com.overcode.controller.dto.bonus;

import com.overcode.controller.dto.bonus.mission.MissionStatusResponseDTO;
import com.overcode.model.bonus.EstadoBonus;

import java.util.List;

public record DailyBonusStatusResponseDTO(
        Integer currentStreak,
        Double nextBonus,
        Boolean alreadyClaimedToday,
        List<Double> weeklyBonusRewards,
        Boolean puedeReclamar,
        MissionStatusResponseDTO misiones
) {
    public static DailyBonusStatusResponseDTO desdeModelo(EstadoBonus estadoBonus) {
        MissionStatusResponseDTO missionDTOs = MissionStatusResponseDTO.desdeModelo(estadoBonus.mision());

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
