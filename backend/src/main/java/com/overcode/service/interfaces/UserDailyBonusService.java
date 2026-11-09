package com.overcode.service.interfaces;

import com.overcode.model.bonus.EstadoBonus;
import com.overcode.model.bonus.PeticionVisitaJugador;
import com.overcode.model.bonus.UserDailyBonus;
import com.overcode.model.user.User;

public interface UserDailyBonusService {
    UserDailyBonus recuperar(Long id);
    UserDailyBonus guardar(UserDailyBonus userDailyBonus);
    void registrarCompra(User user);
    EstadoBonus registrarVisitaJugador(PeticionVisitaJugador peticion);
}
