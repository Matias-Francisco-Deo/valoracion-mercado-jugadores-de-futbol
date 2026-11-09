package com.overcode.persistence.repository.interfaces;

import com.overcode.model.bonus.UserDailyBonus;
import com.overcode.model.user.User;

import java.util.Optional;

public interface UserDiaryBonusRepository {
    UserDailyBonus guardar(UserDailyBonus userDailyBonus);
    Optional<UserDailyBonus> recuperarPorId(Long id);
    Optional<UserDailyBonus> recuperarPorUsuario(User user);
    Optional<UserDailyBonus> recuperarPorUserId(Long userId);
    void eliminarPorId(Long id);
}
