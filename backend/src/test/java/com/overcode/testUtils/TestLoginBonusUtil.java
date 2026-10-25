package com.overcode.testUtils;

import com.overcode.model.bonus.LoginBonus;
import com.overcode.model.bonus.LoginBonusRewards;

import java.util.List;

public class TestLoginBonusUtil {

    public static LoginBonus getLoginBonusBase() {
        LoginBonusRewards rewardsBase = new LoginBonusRewards(List.of(1.0, 2.0, 2.0, 5.0, 5.0, 10.0, 15.0));
        return new LoginBonus("Bonus Base Semanal", rewardsBase);
    }

    public static LoginBonus getLoginBonusPersonalizado(List<Double> rewards) {
        LoginBonusRewards rewardsBase = new LoginBonusRewards(rewards);
        return new LoginBonus("Bonus Personalizado", rewardsBase);
    }
}
