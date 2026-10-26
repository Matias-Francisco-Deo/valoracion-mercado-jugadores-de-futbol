package com.overcode.service.impl;

import com.overcode.model.User;
import com.overcode.model.bonus.BonusClaim;
import com.overcode.model.bonus.EstadoBonus;
import com.overcode.model.bonus.LoginBonus;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.service.interfaces.DailyBonusService;
import com.overcode.service.interfaces.LoginBonusService;
import com.overcode.service.interfaces.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DailyBonusServiceImpl implements DailyBonusService {

    private final UserRepository userRepository;
    private final LoginBonusService loginBonusService;
    private final UserService userService;

    public DailyBonusServiceImpl(UserRepository userRepository,
                                 LoginBonusService loginBonusService, UserService userService) {
        this.userRepository = userRepository;
        this.loginBonusService = loginBonusService;
        this.userService = userService;
    }

    @Override
    public BonusClaim reclamarBonus(User user) {
        User userRecuperado = userService.recuperar(user.getId());

        LoginBonus loginBonus = loginBonusService.recuperarSeleccionado();

        BonusClaim reclamoBonus = userRecuperado.reclamarBonusDiario(loginBonus);
        userRepository.guardar(userRecuperado);

        return reclamoBonus;
    }

    @Override
    @Transactional(readOnly = true)
    public EstadoBonus obtenerEstadoBonus(User user) {
        User userRecuperado = userService.recuperar(user.getId());

        LoginBonus loginBonus = loginBonusService.recuperarSeleccionado();

        return loginBonus.obtenerEstadoBonus(userRecuperado); // TODO método de user
    }
}
