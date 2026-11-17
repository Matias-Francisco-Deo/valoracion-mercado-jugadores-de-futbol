package com.overcode.model.bonus.mission;

import com.overcode.model.player.Player;

public class MissionEngine {
    private MissionEngine() {
        /* This utility class should not be instantiated */
    }


    public static void registrarVisitaJugadorRecursivamente(DailyBonusMission<?> dailyBonusMission, Player player) {

        dailyBonusMission.forEach(mission -> {
                    switch (mission) {
                        case Visit3PlayersDailyBonusMission v3pm:
                            v3pm.complete(player);
                            break;
                        default:
                            break;
                    }
                }
            );
    }

    public static void registrarCompraRecursivamente(DailyBonusMission<?> dailyBonusMission) {
        dailyBonusMission.forEach(mission -> {
                    switch (mission) {
                        case BuyDailyBonusMission bm:
                            bm.complete();
                            break;
                        default:
                            break;
                    }
                }
        );
    }
}
