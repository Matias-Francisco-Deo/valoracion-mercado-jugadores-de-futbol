package com.overcode.service.impl.bonus;

import com.overcode.model.bonus.DailyBonus;
import com.overcode.model.bonus.EstadoBonus;
import com.overcode.model.bonus.PeticionVisitaJugador;
import com.overcode.model.bonus.UserDailyBonus;
import com.overcode.model.user.User;
import com.overcode.persistence.repository.impl.PlayerRepositoryImpl;
import com.overcode.persistence.repository.interfaces.UserDailyBonusRepository;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.interfaces.DailyBonusService;
import com.overcode.service.interfaces.UserDailyBonusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserDailyBonusServiceImpl implements UserDailyBonusService {

    private final UserDailyBonusRepository userDailyBonusRepository;
    private final DailyBonusService dailyBonusService;
    private final PlayerRepositoryImpl playerRepositoryImpl;


    public UserDailyBonusServiceImpl(UserDailyBonusRepository userDailyBonusRepository, DailyBonusService dailyBonusService, PlayerRepositoryImpl playerRepositoryImpl) {
        this.userDailyBonusRepository = userDailyBonusRepository;
        this.dailyBonusService = dailyBonusService;
        this.playerRepositoryImpl = playerRepositoryImpl;
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
        Optional<UserDailyBonus> userDailyBonus = userDailyBonusRepository.recuperarPorUserId(user.getId());
        // error not found?
        userDailyBonus.ifPresent(bonus -> {
            bonus.registrarCompra();
            userDailyBonusRepository.guardar(bonus);
        });
    }

    @Override
    public EstadoBonus registrarVisitaJugador(PeticionVisitaJugador peticion) {
        Long playerId = peticion.player().getId();
        Long userId = peticion.user().getId();

        if (!playerRepositoryImpl.existsById(playerId)) {
            throw new EntidadNoEncontradaException("El jugador no existe: " + playerId);
        }

        DailyBonus dailyBonus = dailyBonusService.recuperarSeleccionado();
        UserDailyBonus userDailyBonus = userDailyBonusRepository.recuperarPorUserId(userId).orElseThrow(
                () -> new EntidadNoEncontradaException("El bonus diario no existe para el usuario: " + userId)
        );

        userDailyBonus.registrarVisitaJugador(peticion.player());

        userDailyBonusRepository.guardar(userDailyBonus);
        return dailyBonus.obtenerEstadoBonus(userDailyBonus);
    }
}
