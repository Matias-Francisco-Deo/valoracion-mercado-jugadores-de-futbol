package com.overcode.testUtils;

import com.overcode.model.bonus.mission.*;
import com.overcode.model.player.Player;

import java.util.Arrays;
import java.util.List;

public class TestMissionUtil {

    public static TestMission<String> crearTestMission(String nombre) {
        return new TestMission<>(nombre);
    }

    public static TestMission<String> crearTestMission(String nombre, boolean completed) {
        return new TestMission<>(nombre, completed);
    }

    public static GenericOrMission<Object> crearOrMission(String nombre, Mission<?>... missions) {
        return new GenericOrMission<>(nombre, Arrays.asList(missions));
    }

    public static GenericAndMission<Object> crearAndMission(String nombre, Mission<?>... missions) {
        return new GenericAndMission<>(nombre, Arrays.asList(missions));
    }

    public static VisitPlayerMission crearVisitPlayerMission(Player player) {
        return new VisitPlayerMission(player);
    }

    public static Visit3PlayersMission crearVisit3PlayersMission(Player p1, Player p2, Player p3) {
        VisitPlayerMission m1 = new VisitPlayerMission(p1);
        VisitPlayerMission m2 = new VisitPlayerMission(p2);
        VisitPlayerMission m3 = new VisitPlayerMission(p3);
        return new Visit3PlayersMission(m1, m2, m3);
    }

    public static BuyMission crearBuyMission() {
        return new BuyMission();
    }

    public static DailyBonusOrMission crearDailyBonusOrMission(BuyMission buyMission, Visit3PlayersMission visit3PlayersMission) {
        return new DailyBonusOrMission(buyMission, visit3PlayersMission);
    }
}
