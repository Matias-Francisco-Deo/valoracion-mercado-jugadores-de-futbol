package com.overcode.service.interfaces;

import com.overcode.model.bonus.mission.Mission;

public interface MissionService {
    Mission<?> guardar(Mission<?> mission);
    Mission<?> recuperarPorId(Long id);
}
