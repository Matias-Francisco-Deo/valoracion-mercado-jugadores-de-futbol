package com.overcode.model.bonus.mission;

import com.overcode.model.player.Player;
import com.overcode.testUtils.TestPlayerUtil;
import com.overcode.testUtils.bonus.mission.TestMissionUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BuyOrVisitMissionTest {

    @Test
    void alClonarMisionCreaNuevasSinId() {
        BuyDailyBonusMission buyMission = TestMissionUtil.crearBuyMission();
        buyMission.setId(1L);
        Player p1 = TestPlayerUtil.getJugadorConNombre("P1");
        Player p2 = TestPlayerUtil.getJugadorConNombre("P2");
        Player p3 = TestPlayerUtil.getJugadorConNombre("P3");
        Visit3PlayersDailyBonusMission visitMission = TestMissionUtil.crearVisit3PlayersMission(p1, p2, p3);
        visitMission.setId(2L);

        BuyOrVisitMission dailyOr = TestMissionUtil.crearDailyBonusOrMission(buyMission, visitMission);
        dailyOr.setId(3L);
        BuyOrVisitMission clonedDailyOr = dailyOr.clonar();

        assertNull(clonedDailyOr.getId());
        assertNotEquals(clonedDailyOr, dailyOr);

        BuyDailyBonusMission clonedBuy = clonedDailyOr.getBuyMission();
        assertNull(clonedBuy.getId());
        assertNotEquals(clonedBuy, buyMission);

        Visit3PlayersDailyBonusMission clonedVisit = clonedDailyOr.getVisit3PlayersMission();
        assertNull(clonedVisit.getId());
        assertNotEquals(clonedVisit, visitMission);
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

        BuyOrVisitMission dailyOr = TestMissionUtil.crearDailyBonusOrMission(buyMission, visitMission);
        assertFalse(dailyOr.isCompleted());

        dailyOr.completeBuy();
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

        BuyOrVisitMission dailyOr = TestMissionUtil.crearDailyBonusOrMission(buyMission, visitMission);
        assertFalse(dailyOr.isCompleted());

        dailyOr.completeVisit(p1);
        dailyOr.completeVisit(p2);
        assertFalse(dailyOr.isCompleted());

        dailyOr.completeVisit(p3);
        assertTrue(dailyOr.isCompleted());
        assertFalse(buyMission.isCompleted());
        assertTrue(visitMission.isCompleted());
    }

    @Test
    void alCompletarTodoSeSatisfaceDailyBonusOrMissionSinComprar() {
        BuyDailyBonusMission buyMission = TestMissionUtil.crearBuyMission();
        Player p1 = TestPlayerUtil.crearJugador("P1", 10.0);
        p1.setId(1L);
        Player p2 = TestPlayerUtil.crearJugador("P2", 20.0);
        p2.setId(2L);
        Player p3 = TestPlayerUtil.crearJugador("P3", 30.0);
        p3.setId(3L);
        Visit3PlayersDailyBonusMission visitMission = TestMissionUtil.crearVisit3PlayersMission(p1, p2, p3);

        BuyOrVisitMission dailyOr = TestMissionUtil.crearDailyBonusOrMission(buyMission, visitMission);
        assertFalse(dailyOr.isCompleted());

        dailyOr.completeVisit(p1);
        dailyOr.completeVisit(p2);
        assertFalse(dailyOr.isCompleted());

        dailyOr.completeVisit(p3);

        dailyOr.completeBuy();

        assertTrue(dailyOr.isCompleted());
        assertTrue(buyMission.isCompleted());
        assertTrue(visitMission.isCompleted());
    }
}
