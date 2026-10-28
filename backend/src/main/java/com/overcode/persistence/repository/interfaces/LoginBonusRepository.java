package com.overcode.persistence.repository.interfaces;

import com.overcode.model.bonus.LoginBonus;

import java.util.List;
import java.util.Optional;

public interface LoginBonusRepository {
    LoginBonus guardar(LoginBonus loginBonus);
    Optional<LoginBonus> recuperarPorId(Long id);
    Optional<LoginBonus> recuperarPorNombre(String nombre);
    boolean existePorId(Long id);
    boolean existePorNombre(String nombre);
    List<LoginBonus> recuperarTodos();
}
