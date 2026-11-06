package com.overcode.persistence.repository.interfaces;

import com.overcode.model.bonus.DiaryBonus;

import java.util.List;
import java.util.Optional;

public interface DiaryBonusRepository {
    DiaryBonus guardar(DiaryBonus diaryBonus);
    Optional<DiaryBonus> recuperarPorId(Long id);
    Optional<DiaryBonus> recuperarPorNombre(String nombre);
    boolean existePorNombre(String nombre);
    List<DiaryBonus> recuperarTodos();
}
