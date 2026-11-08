package com.overcode.service.impl;

import com.overcode.model.bonus.BonusClaim;
import com.overcode.model.bonus.DiaryBonus;
import com.overcode.model.bonus.EstadoBonus;
import com.overcode.model.bonus.PeticionVisitaJugador;
import com.overcode.model.bonus.UserDiaryBonus;
import com.overcode.model.user.User;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.interfaces.BonusService;
import com.overcode.service.interfaces.DiaryBonusService;
import com.overcode.service.interfaces.UserDiaryBonusService;
import com.overcode.service.interfaces.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BonusServiceImpl implements BonusService {

    private final UserRepository userRepository;
    private final DiaryBonusService diaryBonusService;
    private final UserService userService;
    private final UserDiaryBonusService userDiaryBonusService;
    private final PlayerRepository playerRepository;

    public BonusServiceImpl(UserRepository userRepository,
                            DiaryBonusService diaryBonusService,
                            UserService userService,
                            UserDiaryBonusService userDiaryBonusService,
                            PlayerRepository playerRepository) {
        this.userRepository = userRepository;
        this.diaryBonusService = diaryBonusService;
        this.userService = userService;
        this.userDiaryBonusService = userDiaryBonusService;
        this.playerRepository = playerRepository;
    }

    @Override
    public BonusClaim reclamarBonus(User user) {
        User userRecuperado = userService.recuperar(user.getId());
        DiaryBonus diaryBonus = diaryBonusService.recuperarSeleccionado();

        UserDiaryBonus userDiaryBonus = userDiaryBonusService.recuperarOPermitirCrear(userRecuperado);
        userDiaryBonus.setUser(userRecuperado);
        BonusClaim reclamoBonus = userDiaryBonus.reclamarBonus(diaryBonus);

        userDiaryBonusService.guardar(userDiaryBonus);
        userRepository.guardar(userRecuperado);

        return reclamoBonus;
    }

    @Override
    public EstadoBonus obtenerEstadoBonus(User user) {
        User userRecuperado = userService.recuperar(user.getId());
        DiaryBonus diaryBonus = diaryBonusService.recuperarSeleccionado();

        UserDiaryBonus userDiaryBonus = userDiaryBonusService.recuperarOPermitirCrear(userRecuperado);
        userDiaryBonus.setUser(userRecuperado);

        return diaryBonus.obtenerEstadoBonus(userDiaryBonus);
    }

    @Override
    public EstadoBonus registrarVisitaJugador(PeticionVisitaJugador peticion) {
        Long playerId = peticion.player().getId();
        if (!playerRepository.existsById(playerId)) {
            throw new EntidadNoEncontradaException("El jugador no existe: " + playerId);
        }

        User userRecuperado = userService.recuperar(peticion.user().getId());
        DiaryBonus diaryBonus = diaryBonusService.recuperarSeleccionado();

        UserDiaryBonus userDiaryBonus = userDiaryBonusService.recuperarOPermitirCrear(userRecuperado);
        userDiaryBonus.setUser(userRecuperado);
        userDiaryBonus.registrarVisitaJugador(peticion.player());

        userDiaryBonusService.guardar(userDiaryBonus);
        return diaryBonus.obtenerEstadoBonus(userDiaryBonus);
    }
}
