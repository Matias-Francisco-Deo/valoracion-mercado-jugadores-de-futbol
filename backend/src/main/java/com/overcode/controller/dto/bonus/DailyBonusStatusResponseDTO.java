package com.overcode.controller.dto.bonus;

import com.overcode.model.bonus.DiaDeLaSemana;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyBonusStatusResponseDTO {
    private Integer currentStreak;
    private Double todayBonus;
    private Boolean alreadyClaimedToday;
    private Map<DiaDeLaSemana, Double> weeklyBonuses;
}
