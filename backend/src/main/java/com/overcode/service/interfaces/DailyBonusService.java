package com.overcode.service.interfaces;

import com.overcode.model.bonus.BonusClaim;
import com.overcode.model.bonus.EstadoBonus;

public interface DailyBonusService {
    BonusClaim reclamarBonus(String username);
    EstadoBonus obtenerEstadoBonus(String username);
}
