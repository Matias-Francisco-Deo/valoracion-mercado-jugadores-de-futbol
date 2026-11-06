package com.overcode.service.interfaces;

import com.overcode.model.bonus.UserDiaryBonus;
import com.overcode.model.user.User;

public interface UserDiaryBonusService {
    UserDiaryBonus recuperarOPermitirCrear(User user);
    UserDiaryBonus guardar(UserDiaryBonus userDiaryBonus);
    void registrarCompra(User user);
}
