package com.overcode.service.impl;

import com.overcode.model.bonus.DiaryBonus;
import com.overcode.model.bonus.UserDiaryBonus;
import com.overcode.model.user.User;
import com.overcode.persistence.repository.interfaces.UserDiaryBonusRepository;
import com.overcode.service.interfaces.DiaryBonusService;
import com.overcode.service.interfaces.UserDiaryBonusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserDiaryBonusServiceImpl implements UserDiaryBonusService {

    private final UserDiaryBonusRepository userDiaryBonusRepository;
    private final DiaryBonusService diaryBonusService;

    public UserDiaryBonusServiceImpl(UserDiaryBonusRepository userDiaryBonusRepository, DiaryBonusService diaryBonusService) {
        this.userDiaryBonusRepository = userDiaryBonusRepository;
        this.diaryBonusService = diaryBonusService;
    }

    @Override
    public UserDiaryBonus recuperarOPermitirCrear(User user) {
        UserDiaryBonus userDiaryBonus = userDiaryBonusRepository.recuperarPorUsuario(user)
                .orElseGet(() -> new UserDiaryBonus(user));

        DiaryBonus diaryBonus = diaryBonusService.recuperarSeleccionado();
        userDiaryBonus.inicializarConBonus(diaryBonus);
        return userDiaryBonusRepository.guardar(userDiaryBonus);
    }

    @Override
    public UserDiaryBonus guardar(UserDiaryBonus userDiaryBonus) {
        return userDiaryBonusRepository.guardar(userDiaryBonus);
    }

    @Override
    public void registrarCompra(User user) {
        UserDiaryBonus userDiaryBonus = recuperarOPermitirCrear(user);
        userDiaryBonus.registrarCompra();
        userDiaryBonusRepository.guardar(userDiaryBonus);
    }
}
