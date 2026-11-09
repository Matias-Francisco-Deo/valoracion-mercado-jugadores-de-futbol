package com.overcode.model.bonus.mission;

import com.overcode.model.player.Player;
import com.overcode.testUtils.TestMissionUtil;
import com.overcode.testUtils.TestPlayerUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BuyDailyBonusMissionTest {

    @Test
    void alCompletarBuyMissionCambiaSuEstadoACompletado() {
        BuyDailyBonusMission buyMission = TestMissionUtil.crearBuyMission();
        assertFalse(buyMission.isCompleted());

        buyMission.complete();
        assertTrue(buyMission.isCompleted());
    }

    @Test
    void alCompletarBuyMissionSeSatisfaceDailyBonusOrMissionSinVisitarJugadores() {
        BuyDailyBonusMission buyMission = TestMissionUtil.crearBuyMission();
        Player p1 = TestPlayerUtil.crearJugador("P1", 10.0);
        p1.setId(1L);
        Player p2 = TestPlayerUtil.crearJugador("P2", 20.0);
        p2.setId(2L);
        Player p3 = TestPlayerUtil.crearJugador("P3", 30.0);
        p3.setId(3L);
        Visit3PlayersDailyBonusMission visitMission = TestMissionUtil.crearVisit3PlayersMission(p1, p2, p3);

        DailyBonusOrDailyBonusMission dailyOr = TestMissionUtil.crearDailyBonusOrMission(buyMission, visitMission);
        assertFalse(dailyOr.isCompleted());

        dailyOr.complete((Void) null);
        assertTrue(dailyOr.isCompleted());
        assertTrue(buyMission.isCompleted());
        assertFalse(visitMission.isCompleted());
    }

    @Test
    void alCompletarVisitasSeSatisfaceDailyBonusOrMissionSinComprar() {
        BuyDailyBonusMission buyMission = TestMissionUtil.crearBuyMission();
        Player p1 = TestPlayerUtil.crearJugador("P1", 10.0);
        p1.setId(1L);
        Player p2 = TestPlayerUtil.crearJugador("P2", 20.0);
        p2.setId(2L);
        Player p3 = TestPlayerUtil.crearJugador("P3", 30.0);
        p3.setId(3L);
        Visit3PlayersDailyBonusMission visitMission = TestMissionUtil.crearVisit3PlayersMission(p1, p2, p3);

        DailyBonusOrDailyBonusMission dailyOr = TestMissionUtil.crearDailyBonusOrMission(buyMission, visitMission);
        assertFalse(dailyOr.isCompleted());

        dailyOr.completeVisit(p1);
        dailyOr.completeVisit(p2);
        assertFalse(dailyOr.isCompleted());

        dailyOr.completeVisit(p3);
        assertTrue(dailyOr.isCompleted());
        assertFalse(buyMission.isCompleted());
        assertTrue(visitMission.isCompleted());
    }
}
