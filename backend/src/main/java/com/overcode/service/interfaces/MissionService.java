package com.overcode.service.interfaces;

import com.overcode.model.bonus.mission.DailyBonusMission;

public interface MissionService {
    DailyBonusMission<?> guardar(DailyBonusMission<?> dailyBonusMission);
    DailyBonusMission<?> recuperarPorId(Long id);
}
