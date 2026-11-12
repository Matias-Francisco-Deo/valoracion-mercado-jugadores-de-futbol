package com.overcode.model.bonus.mission;

import com.overcode.model.player.Player;

public class MissionEngine {
    private MissionEngine() {
        /* This utility class should not be instantiated */
    }


    public static void registrarVisitaJugadorRecursivamente(DailyBonusMission<?> dailyBonusMission, Player player) {
        switch (dailyBonusMission) {
            case Visit3PlayersDailyBonusMission v3pm:
                v3pm.complete(player);
                break;
            case VisitPlayerDailyBonusMission vpm:
                vpm.complete(player);
                break;
            default:
                dailyBonusMission.getSubmissions().forEach(subm -> registrarVisitaJugadorRecursivamente(subm, player));
        }
    }

    public static void registrarCompraRecursivamente(DailyBonusMission<?> dailyBonusMission) {
        switch (dailyBonusMission) {
            case BuyDailyBonusMission bm:
                bm.complete();
                break;
            default:
                dailyBonusMission.getSubmissions().forEach(MissionEngine::registrarCompraRecursivamente);
        }
    }
}
