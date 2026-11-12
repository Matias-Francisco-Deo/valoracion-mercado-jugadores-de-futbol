package com.overcode.model.bonus.mission;

import com.overcode.model.player.Player;
import com.overcode.testUtils.bonus.mission.TestDailyBonusMission;
import com.overcode.testUtils.bonus.mission.TestMissionUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.overcode.testUtils.TestPlayerUtil.getJugadorConNombreYId;
import static com.overcode.testUtils.bonus.mission.TestMissionUtil.crearTestMission;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MissionEngineTest {
    Player player1 = getJugadorConNombreYId(1L, "pepe");
    Player player2 = getJugadorConNombreYId(2L, "pepa");
    Player player3 = getJugadorConNombreYId(3L, "pepito");

    @Test
    void visitarRecursivamenteCompletaUnaMisionEnProfundidad0() {
        Visit3PlayersDailyBonusMission visitMission = TestMissionUtil.crearVisit3PlayersMission(player1, player2, player3);
        assertFalse(visitMission.isCompleted());

        MissionEngine.registrarVisitaJugadorRecursivamente(visitMission, player1);
        MissionEngine.registrarVisitaJugadorRecursivamente(visitMission, player2);
        MissionEngine.registrarVisitaJugadorRecursivamente(visitMission, player3);
        assertTrue(visitMission.isCompleted());
    }

    @Test
    void visitarRecursivamenteCompletaUnaMisionEnProfundidadN() {
        Visit3PlayersDailyBonusMission visitMission = TestMissionUtil.crearVisit3PlayersMission(player1, player2, player3);

        TestDailyBonusMission testDailyBonusMission = crearTestMission();
        testDailyBonusMission.setSubmissions(List.of(visitMission));

        assertFalse(visitMission.isCompleted());

        MissionEngine.registrarVisitaJugadorRecursivamente(testDailyBonusMission, player1);
        MissionEngine.registrarVisitaJugadorRecursivamente(testDailyBonusMission, player2);
        MissionEngine.registrarVisitaJugadorRecursivamente(testDailyBonusMission, player3);
        assertTrue(visitMission.isCompleted());
    }

    @Test
    void comprarRecursivamenteCompletaUnaMisionEnProfundidad0() {
        BuyDailyBonusMission buyMission = TestMissionUtil.crearBuyMission();
        assertFalse(buyMission.isCompleted());

        MissionEngine.registrarCompraRecursivamente(buyMission);
        assertTrue(buyMission.isCompleted());
    }

    @Test
    void comprarRecursivamenteCompletaUnaMisionEnProfundidadN() {
        BuyDailyBonusMission buyMission = TestMissionUtil.crearBuyMission();

        TestDailyBonusMission testDailyBonusMission = crearTestMission();
        testDailyBonusMission.setSubmissions(List.of(buyMission));

        assertFalse(buyMission.isCompleted());

        MissionEngine.registrarCompraRecursivamente(buyMission);
        assertTrue(buyMission.isCompleted());
    }

}
