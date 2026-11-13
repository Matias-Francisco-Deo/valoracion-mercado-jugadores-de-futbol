package com.overcode.testUtils;

import com.overcode.model.bonus.DailyBonus;
import com.overcode.model.bonus.DailyBonusRewards;
import com.overcode.model.bonus.mission.DailyBonusMission;
import com.overcode.testUtils.bonus.mission.TestDailyBonusMission;

import java.util.List;

public class TestDailyBonusUtil {

    public static DailyBonus getDailyBonusBase() {
        DailyBonusRewards rewardsBase = new DailyBonusRewards(List.of(1.0, 2.0, 2.0, 5.0, 5.0, 10.0, 15.0));
        DailyBonusMission<?> mission = new TestDailyBonusMission();
        mission.setNombre("Misión test base");
        return new DailyBonus("Bonus Base Semanal", rewardsBase, mission);
    }

    public static DailyBonus getDailyBonusBaseConNombre(String nombre) {
        DailyBonusRewards rewardsBase = new DailyBonusRewards(List.of(1.0, 2.0, 2.0, 5.0, 5.0, 10.0, 15.0));
        DailyBonusMission<?> mission = new TestDailyBonusMission();
        mission.setNombre("Misión test base");
        return new DailyBonus(nombre, rewardsBase, mission);
    }

    public static DailyBonus getDailyBonusBaseYaCompletado() {
        DailyBonusRewards rewardsBase = new DailyBonusRewards(List.of(1.0, 2.0, 2.0, 5.0, 5.0, 10.0, 15.0));
        DailyBonusMission<?> mission = new TestDailyBonusMission();
        mission.setNombre("Misión test base");
        mission.complete(null);

        return new DailyBonus("Bonus Base Semanal", rewardsBase, mission);
    }

    public static DailyBonus getDailyBonusBaseConMision(DailyBonusMission<?> rootDailyBonusMission) {
        DailyBonusRewards rewardsBase = new DailyBonusRewards(List.of(1.0, 2.0, 2.0, 5.0, 5.0, 10.0, 15.0));
        return new DailyBonus("Bonus Base Semanal", rewardsBase, rootDailyBonusMission);
    }
}
