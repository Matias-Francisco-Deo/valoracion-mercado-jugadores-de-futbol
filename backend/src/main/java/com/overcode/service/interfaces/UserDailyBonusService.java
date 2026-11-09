package com.overcode.service.interfaces;

import com.overcode.model.bonus.UserDailyBonus;
import com.overcode.model.user.User;

public interface UserDailyBonusService {
    UserDailyBonus recuperarOPermitirCrear(User user);
    UserDailyBonus guardar(UserDailyBonus userDailyBonus);
    void registrarCompra(User user);
}
