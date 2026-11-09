package com.overcode.persistence.repository.interfaces;

import com.overcode.model.bonus.UserDailyBonus;

import java.util.Optional;

public interface UserDailyBonusRepository {
    UserDailyBonus guardar(UserDailyBonus userDailyBonus);
    Optional<UserDailyBonus> recuperarPorId(Long id);
    Optional<UserDailyBonus> recuperarPorUserId(Long userId);
    void eliminarPorId(Long id);
}
