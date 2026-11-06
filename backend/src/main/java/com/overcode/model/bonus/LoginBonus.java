package com.overcode.model.bonus;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class LoginBonus extends DiaryBonus {

    public LoginBonus(Long id, String nombre, LoginBonusRewards bonusRewards) {
        super(id, nombre, bonusRewards);
    }

    public LoginBonus(String nombre, LoginBonusRewards bonusRewards) {
        super(nombre, bonusRewards);
    }
}
