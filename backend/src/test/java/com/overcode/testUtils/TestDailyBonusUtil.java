package com.overcode.testUtils;

import com.overcode.model.bonus.DailyBonus;
import com.overcode.model.bonus.DailyBonusRewards;
import com.overcode.model.bonus.mission.DailyBonusMission;

import java.util.List;

public class TestDailyBonusUtil {

    public static DailyBonus getDiaryBonusBase() {
        DailyBonusRewards rewardsBase = new DailyBonusRewards(List.of(1.0, 2.0, 2.0, 5.0, 5.0, 10.0, 15.0));
        return new DailyBonus("Bonus Base Semanal", rewardsBase);
    }

    public static DailyBonus getDiaryBonusBase(DailyBonusMission<?> rootDailyBonusMission) {
        DailyBonusRewards rewardsBase = new DailyBonusRewards(List.of(1.0, 2.0, 2.0, 5.0, 5.0, 10.0, 15.0));
        return new DailyBonus("Bonus Base Semanal", rewardsBase, rootDailyBonusMission);
    }

    public static DailyBonus getLoginBonusBase() {
        return getDiaryBonusBase();
    }
}
