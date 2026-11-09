package com.overcode.service.impl.bonus;

import com.overcode.model.bonus.DailyBonus;
import com.overcode.model.bonus.UserDailyBonus;
import com.overcode.model.user.User;
import com.overcode.persistence.repository.interfaces.UserDiaryBonusRepository;
import com.overcode.service.interfaces.DiaryBonusService;
import com.overcode.service.interfaces.UserDailyBonusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserDailyBonusServiceImpl implements UserDailyBonusService {

    private final UserDiaryBonusRepository userDiaryBonusRepository;
    private final DiaryBonusService diaryBonusService;

    public UserDailyBonusServiceImpl(UserDiaryBonusRepository userDiaryBonusRepository, DiaryBonusService diaryBonusService) {
        this.userDiaryBonusRepository = userDiaryBonusRepository;
        this.diaryBonusService = diaryBonusService;
    }

    @Override
    public UserDailyBonus recuperarOPermitirCrear(User user) { // TODO NO es parte del user y NUNCA por fuera
        UserDailyBonus userDailyBonus = userDiaryBonusRepository.recuperarPorUsuario(user)
                .orElseGet(() -> new UserDailyBonus(user));

        DailyBonus dailyBonus = diaryBonusService.recuperarSeleccionado();
        userDailyBonus.inicializarConBonus(dailyBonus);
        return userDiaryBonusRepository.guardar(userDailyBonus);
    }

    @Override
    public UserDailyBonus guardar(UserDailyBonus userDailyBonus) {
        return userDiaryBonusRepository.guardar(userDailyBonus);
    }

    @Override
    public void registrarCompra(User user) {
        UserDailyBonus userDailyBonus = recuperarOPermitirCrear(user);
        userDailyBonus.registrarCompra();
        userDiaryBonusRepository.guardar(userDailyBonus);
    }
}
