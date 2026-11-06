package com.overcode.persistence.repository.interfaces;

import com.overcode.model.bonus.UserDiaryBonus;
import com.overcode.model.user.User;

import java.util.Optional;

public interface UserDiaryBonusRepository {
    UserDiaryBonus guardar(UserDiaryBonus userDiaryBonus);
    Optional<UserDiaryBonus> recuperarPorId(Long id);
    Optional<UserDiaryBonus> recuperarPorUsuario(User user);
    Optional<UserDiaryBonus> recuperarPorUserId(Long userId);
    void eliminarPorId(Long id);
}
