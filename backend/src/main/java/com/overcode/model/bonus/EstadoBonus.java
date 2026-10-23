package com.overcode.model.bonus;

import java.util.Map;

public record EstadoBonus(Integer currentStreak,
                          Double todayBonus,
                          Boolean alreadyClaimedToday,
                          Map<DiaDeLaSemana, Double> weeklyBonuses) {
}
