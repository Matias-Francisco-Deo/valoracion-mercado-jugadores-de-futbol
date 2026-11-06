package com.overcode.service.impl;

import com.overcode.model.bonus.LoginBonus;
import com.overcode.model.bonus.DiaryBonus;
import com.overcode.service.interfaces.DiaryBonusService;
import com.overcode.service.interfaces.LoginBonusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LoginBonusServiceImpl implements LoginBonusService {

    private final DiaryBonusService diaryBonusService;

    public LoginBonusServiceImpl(DiaryBonusService diaryBonusService) {
        this.diaryBonusService = diaryBonusService;
    }

    @Override
    public LoginBonus recuperarSeleccionado() {
        DiaryBonus diaryBonus = diaryBonusService.recuperarSeleccionado();
        if (diaryBonus instanceof LoginBonus loginBonus) {
            return loginBonus;
        }
        if (diaryBonus == null) {
            return null;
        }
        return new LoginBonus(diaryBonus.getId(), diaryBonus.getNombre(), new com.overcode.model.bonus.LoginBonusRewards(diaryBonus.getBonusRewards().getRewards()));
    }
}
