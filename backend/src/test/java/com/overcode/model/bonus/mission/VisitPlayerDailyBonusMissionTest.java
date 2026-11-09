package com.overcode.model.bonus.mission;

import com.overcode.model.player.Player;
import com.overcode.testUtils.TestMissionUtil;
import com.overcode.testUtils.TestPlayerUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VisitPlayerDailyBonusMissionTest {

    @Test
    void alVisitarJugadorObjetivoSeMarcaComoCompletada() {
        Player messi = TestPlayerUtil.crearJugador("Messi", 100.0);
        messi.setId(10L);

        VisitPlayerDailyBonusMission mision = TestMissionUtil.crearVisitPlayerMission(messi);
        assertFalse(mision.isCompleted());

        mision.complete(messi);
        assertTrue(mision.isCompleted());
    }

    @Test
    void alVisitarOtroJugadorNoSeCompletaLaMision() {
        Player messi = TestPlayerUtil.crearJugador("Messi", 100.0);
        messi.setId(10L);
        Player ronaldo = TestPlayerUtil.crearJugador("Ronaldo", 90.0);
        ronaldo.setId(7L);

        VisitPlayerDailyBonusMission mision = TestMissionUtil.crearVisitPlayerMission(messi);
        mision.complete(ronaldo);

        assertFalse(mision.isCompleted());
    }

    @Test
    void visit3PlayersMissionSeCompletaSoloCuandoSeVisitanLosTresJugadores() {
        Player p1 = TestPlayerUtil.crearJugador("P1", 10.0);
        p1.setId(1L);
        Player p2 = TestPlayerUtil.crearJugador("P2", 20.0);
        p2.setId(2L);
        Player p3 = TestPlayerUtil.crearJugador("P3", 30.0);
        p3.setId(3L);

        Visit3PlayersDailyBonusMission mision3 = TestMissionUtil.crearVisit3PlayersMission(p1, p2, p3);
        assertFalse(mision3.isCompleted());

        mision3.complete(p1);
        assertFalse(mision3.isCompleted());

        mision3.complete(p2);
        assertFalse(mision3.isCompleted());

        mision3.complete(p3);
        assertTrue(mision3.isCompleted());
    }
}
