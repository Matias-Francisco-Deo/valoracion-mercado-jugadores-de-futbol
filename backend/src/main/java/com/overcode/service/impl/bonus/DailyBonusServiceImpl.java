package com.overcode.service.impl.bonus;

import com.overcode.model.bonus.*;
import com.overcode.model.user.User;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.interfaces.DiaryBonusService;
import com.overcode.service.interfaces.UserDailyBonusService;
import com.overcode.service.interfaces.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DailyBonusServiceImpl implements DailyBonusService {

    private final UserRepository userRepository;
    private final DiaryBonusService diaryBonusService;
    private final UserService userService;
    private final UserDailyBonusService userDailyBonusService;
    private final PlayerRepository playerRepository;

    public DailyBonusServiceImpl(UserRepository userRepository,
                                 DiaryBonusService diaryBonusService,
                                 UserService userService,
                                 UserDailyBonusService userDailyBonusService,
                                 PlayerRepository playerRepository) {
        this.userRepository = userRepository;
        this.diaryBonusService = diaryBonusService;
        this.userService = userService;
        this.userDailyBonusService = userDailyBonusService;
        this.playerRepository = playerRepository;
    }

    @Override
    public BonusClaim reclamarBonus(User user) {
        User userRecuperado = userService.recuperar(user.getId());
        DailyBonus dailyBonus = diaryBonusService.recuperarSeleccionado();

        UserDailyBonus userDailyBonus = userDailyBonusService.recuperarOPermitirCrear(userRecuperado); // TODO NO es parte del user y NUNCA por fuera
        userDailyBonus.setUser(userRecuperado);
        BonusClaim reclamoBonus = userDailyBonus.reclamarBonus(dailyBonus);

        userDailyBonusService.guardar(userDailyBonus);
        userRepository.guardar(userRecuperado);

        return reclamoBonus;
    }

    @Override
    public EstadoBonus obtenerEstadoBonus(User user) {
        User userRecuperado = userService.recuperar(user.getId());
        DailyBonus dailyBonus = diaryBonusService.recuperarSeleccionado();

        UserDailyBonus userDailyBonus = userDailyBonusService.recuperarOPermitirCrear(userRecuperado);
        userDailyBonus.setUser(userRecuperado);

        return dailyBonus.obtenerEstadoBonus(userDailyBonus);
    }

    @Override
    public EstadoBonus registrarVisitaJugador(PeticionVisitaJugador peticion) { // TODO mover a UserDiaryBonusService
        Long playerId = peticion.player().getId();
        if (!playerRepository.existsById(playerId)) {
            throw new EntidadNoEncontradaException("El jugador no existe: " + playerId);
        }

        User userRecuperado = userService.recuperar(peticion.user().getId());
        DailyBonus dailyBonus = diaryBonusService.recuperarSeleccionado();

        UserDailyBonus userDailyBonus = userDailyBonusService.recuperarOPermitirCrear(userRecuperado); // TODO NO es parte del user y NUNCA por fuera
        userDailyBonus.setUser(userRecuperado); // TODO NO es parte del user y NUNCA por fuera
        userDailyBonus.registrarVisitaJugador(peticion.player());

        userDailyBonusService.guardar(userDailyBonus);
        return dailyBonus.obtenerEstadoBonus(userDailyBonus);
    }
}
