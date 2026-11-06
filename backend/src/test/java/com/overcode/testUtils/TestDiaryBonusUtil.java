package com.overcode.testUtils;

import com.overcode.model.bonus.DiaryBonus;
import com.overcode.model.bonus.DiaryBonusRewards;
import com.overcode.model.bonus.mission.Mission;

import java.util.List;

public class TestDiaryBonusUtil {

    public static DiaryBonus getDiaryBonusBase() {
        DiaryBonusRewards rewardsBase = new DiaryBonusRewards(List.of(1.0, 2.0, 2.0, 5.0, 5.0, 10.0, 15.0));
        return new DiaryBonus("Bonus Base Semanal", rewardsBase);
    }

    public static DiaryBonus getDiaryBonusBase(Mission<?> rootMission) {
        DiaryBonusRewards rewardsBase = new DiaryBonusRewards(List.of(1.0, 2.0, 2.0, 5.0, 5.0, 10.0, 15.0));
        return new DiaryBonus("Bonus Base Semanal", rewardsBase, rootMission);
    }

    public static DiaryBonus getLoginBonusBase() {
        return getDiaryBonusBase();
    }
}
