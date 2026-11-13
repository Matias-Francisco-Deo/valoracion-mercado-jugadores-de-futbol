package com.overcode.service.impl.bonus;

import com.overcode.model.bonus.*;
import com.overcode.model.user.User;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import com.overcode.persistence.repository.interfaces.UserDailyBonusRepository;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.interfaces.DailyBonusService;
import com.overcode.service.interfaces.UserDailyBonusService;
import com.overcode.service.interfaces.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserDailyBonusServiceImpl implements UserDailyBonusService {

    private final UserDailyBonusRepository userDailyBonusRepository;
    private final DailyBonusService dailyBonusService;
    private final PlayerRepository playerRepository;
    private final UserService userService;
    private final UserRepository userRepository;


    public UserDailyBonusServiceImpl(UserDailyBonusRepository userDailyBonusRepository, DailyBonusService dailyBonusService, PlayerRepository playerRepository, UserService userService, UserRepository userRepository) {
        this.userDailyBonusRepository = userDailyBonusRepository;
        this.dailyBonusService = dailyBonusService;
        this.playerRepository = playerRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public UserDailyBonus recuperar(Long id) {
        return userDailyBonusRepository.recuperarPorId(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("El bonus diario no existe: " + id));
    }

    @Override
    public UserDailyBonus guardar(UserDailyBonus userDailyBonus) {
        return userDailyBonusRepository.guardar(userDailyBonus);
    }

    @Override
    public void registrarCompra(User user) {
        UserDailyBonus userDailyBonus = userDailyBonusRepository.recuperarPorUserId(user.getId()).orElseThrow(
                () -> new EntidadNoEncontradaException("El bonus diario no existe para el usuario: " + user.getId())
        );
        DailyBonus dailyBonus = dailyBonusService.recuperarSeleccionado();

        userDailyBonus.registrarCompra(dailyBonus);
        userDailyBonusRepository.guardar(userDailyBonus);

    }

    @Override
    public EstadoBonus registrarVisitaJugador(PeticionVisitaJugador peticion) {
        Long playerId = peticion.player().getId();
        Long userId = peticion.user().getId();

        if (!playerRepository.existsById(playerId)) {
            throw new EntidadNoEncontradaException("El jugador no existe: " + playerId);
        }

        DailyBonus dailyBonus = dailyBonusService.recuperarSeleccionado();
        UserDailyBonus userDailyBonus = userDailyBonusRepository.recuperarPorUserId(userId).orElseThrow(
                () -> new EntidadNoEncontradaException("El bonus diario no existe para el usuario: " + userId)
        );

        userDailyBonus.registrarVisitaJugador(peticion.player(), dailyBonus);

        userDailyBonusRepository.guardar(userDailyBonus);
        return dailyBonus.obtenerEstadoBonus(userDailyBonus);
    }

    @Override
    public BonusClaim reclamarBonus(User user) {
        User userRecuperado = userService.recuperar(user.getId());
        UserDailyBonus userDailyBonus = userRecuperado.getUserDailyBonus();
        DailyBonus dailyBonus = dailyBonusService.recuperarSeleccionado();

        BonusClaim reclamoBonus = userDailyBonus.reclamarBonus(dailyBonus);

        userRepository.guardar(userRecuperado);

        return reclamoBonus;
    }

    @Override
    public EstadoBonus obtenerEstadoBonus(User user) {
        UserDailyBonus userDailyBonus = userDailyBonusRepository.recuperarPorUserId(user.getId()).orElseThrow(
                () -> new EntidadNoEncontradaException("Bonus diario no encontrado para usuario: " + user.getId())
        );
        DailyBonus dailyBonus = dailyBonusService.recuperarSeleccionado();

        return dailyBonus.obtenerEstadoBonus(userDailyBonus);
    }
}
