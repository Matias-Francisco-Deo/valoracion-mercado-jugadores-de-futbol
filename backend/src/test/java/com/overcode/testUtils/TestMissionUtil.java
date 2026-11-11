package com.overcode.testUtils;

import com.overcode.model.bonus.mission.*;
import com.overcode.model.player.Player;
import com.overcode.testUtils.bonus.mission.TestDailyBonusMission;

import java.util.Arrays;

public class TestMissionUtil {

    public static TestDailyBonusMission crearTestMission(String nombre) {
        return new TestDailyBonusMission(nombre);
    }

    public static TestDailyBonusMission crearTestMission(String nombre, boolean completed) {
        return new TestDailyBonusMission(nombre, completed);
    }

    public static GenericOrDailyBonusMission<Object> crearOrMission(String nombre, DailyBonusMission<?>... dailyBonusMissions) {
        return new GenericOrDailyBonusMission<>(nombre, Arrays.asList(dailyBonusMissions));
    }

    public static GenericAndDailyBonusMission<Object> crearAndMission(String nombre, DailyBonusMission<?>... dailyBonusMissions) {
        return new GenericAndDailyBonusMission<>(nombre, Arrays.asList(dailyBonusMissions));
    }

    public static VisitPlayerDailyBonusMission crearVisitPlayerMission(Player player) {
        return new VisitPlayerDailyBonusMission(player);
    }

    public static Visit3PlayersDailyBonusMission crearVisit3PlayersMission(Player p1, Player p2, Player p3) {
        VisitPlayerDailyBonusMission m1 = new VisitPlayerDailyBonusMission(p1);
        VisitPlayerDailyBonusMission m2 = new VisitPlayerDailyBonusMission(p2);
        VisitPlayerDailyBonusMission m3 = new VisitPlayerDailyBonusMission(p3);
        return new Visit3PlayersDailyBonusMission(m1, m2, m3);
    }

    public static BuyDailyBonusMission crearBuyMission() {
        return new BuyDailyBonusMission();
    }

    public static BuyOrVisitMission crearDailyBonusOrMission(BuyDailyBonusMission buyMission, Visit3PlayersDailyBonusMission visit3PlayersMission) {
        return new BuyOrVisitMission(buyMission, visit3PlayersMission);
    }
}
