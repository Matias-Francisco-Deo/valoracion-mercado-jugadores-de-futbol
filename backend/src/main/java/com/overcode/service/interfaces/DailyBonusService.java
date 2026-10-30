package com.overcode.service.interfaces;

import com.overcode.model.bonus.BonusClaim;
import com.overcode.model.bonus.EstadoBonus;
import com.overcode.model.user.User;

public interface DailyBonusService {
    BonusClaim reclamarBonus(User user);
    EstadoBonus obtenerEstadoBonus(User user);
}
