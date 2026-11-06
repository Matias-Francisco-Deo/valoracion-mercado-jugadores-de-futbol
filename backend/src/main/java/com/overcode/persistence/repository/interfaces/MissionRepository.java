package com.overcode.persistence.repository.interfaces;

import com.overcode.model.bonus.mission.Mission;

import java.util.Optional;

public interface MissionRepository {
    Mission<?> guardar(Mission<?> mission);
    Optional<Mission<?>> recuperarPorId(Long id);
    void eliminarPorId(Long id);
}
