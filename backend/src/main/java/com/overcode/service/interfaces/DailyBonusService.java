package com.overcode.service.interfaces;

import com.overcode.model.User;
import com.overcode.model.bonus.BonusClaim;
import com.overcode.model.bonus.EstadoBonus;

public interface DailyBonusService {
    BonusClaim reclamarBonus(User user);
    EstadoBonus obtenerEstadoBonus(User user);
}
