package com.overcode.persistence.repository.interfaces;

import com.overcode.model.bonus.DailyBonus;

import java.util.List;
import java.util.Optional;

public interface DailyBonusRepository {
    DailyBonus guardar(DailyBonus dailyBonus);
    Optional<DailyBonus> recuperarPorId(Long id);
    Optional<DailyBonus> recuperarPorNombre(String nombre);
    boolean existePorNombre(String nombre);
    List<DailyBonus> recuperarTodos();
}
