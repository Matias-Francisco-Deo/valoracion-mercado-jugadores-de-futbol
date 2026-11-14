package com.overcode.persistence.repository.interfaces;

import com.overcode.model.bonus.mission.DailyBonusMission;

import java.util.Optional;

public interface DailyBonusMissionRepository {
    DailyBonusMission<?> guardar(DailyBonusMission<?> dailyBonusMission);
    Optional<DailyBonusMission<?>> recuperarPorId(Long id);
    void eliminarPorId(Long id);
}
