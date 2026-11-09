package com.overcode.service.interfaces;

import com.overcode.model.bonus.DailyBonus;

public interface DailyBonusService {
    DailyBonus recuperarSeleccionado();
    DailyBonus guardar(DailyBonus dailyBonus);
}
