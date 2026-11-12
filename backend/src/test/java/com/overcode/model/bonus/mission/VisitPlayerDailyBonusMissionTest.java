package com.overcode.model.bonus.mission;

import com.overcode.model.player.Player;
import com.overcode.testUtils.TestPlayerUtil;
import com.overcode.testUtils.bonus.mission.TestMissionUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VisitPlayerDailyBonusMissionTest {

    @Test
    void clonarMisionDaNuevaSinId() {
        Player messi = TestPlayerUtil.crearJugador("Messi", 100.0);
        VisitPlayerDailyBonusMission mision = TestMissionUtil.crearVisitPlayerMission(messi);
        mision.setId(1L);

        VisitPlayerDailyBonusMission clonada = mision.clonar();

        assertNull(clonada.getId());
        assertNotEquals(clonada, mision);
    }

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
    void clonarMisionDaNuevaConTodasLasSubmisionesClonadas() {
        Player p1 = TestPlayerUtil.crearJugador("P1", 10.0);
        Player p2 = TestPlayerUtil.crearJugador("P2", 20.0);
        Player p3 = TestPlayerUtil.crearJugador("P3", 30.0);

        Visit3PlayersDailyBonusMission mision = TestMissionUtil.crearVisit3PlayersMission(p1, p2, p3);

        Visit3PlayersDailyBonusMission clonada = mision.clonar();

        assertNull(clonada.getId());
        assertNotEquals(clonada, mision.getSubmissions().getFirst());

        DailyBonusMission<?> firstVisit = clonada.getSubmissions().getFirst();
        assertNull(firstVisit.getId());
        assertNotEquals(firstVisit, mision.getSubmissions().get(1));

        DailyBonusMission<?> secondVisit = clonada.getSubmissions().get(1);
        assertNull(secondVisit.getId());
        assertNotEquals(secondVisit, mision);

        DailyBonusMission<?> thirdVisit = clonada.getSubmissions().get(2);
        assertNull(thirdVisit.getId());
        assertNotEquals(thirdVisit, mision.getSubmissions().get(2));

    }

    @Test
    void visit3PlayersMissionNoSeCompletaSiUnoDeLosJugadoresNoEsCorrecto() {
        Player p1 = TestPlayerUtil.crearJugador("P1", 10.0);
        p1.setId(1L);
        Player p2 = TestPlayerUtil.crearJugador("P2", 20.0);
        p2.setId(2L);
        Player p3 = TestPlayerUtil.crearJugador("P3", 30.0);
        p3.setId(3L);
        Player p4 = TestPlayerUtil.crearJugador("P4", 40.0);
        p4.setId(4L);

        Visit3PlayersDailyBonusMission mision3 = TestMissionUtil.crearVisit3PlayersMission(p1, p2, p4);
        assertFalse(mision3.isCompleted());

        mision3.complete(p1);
        assertFalse(mision3.isCompleted());

        mision3.complete(p2);
        assertFalse(mision3.isCompleted());

        mision3.complete(p3);
        assertFalse(mision3.isCompleted());
    }

    @Test
    void visit3PlayersMissionNoSeCompletaSiUnoDeLosJugadoresFalta() {
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
    }

    @Test
    void visit3PlayersMissionSeCompletaSoloCuandoSeVisitanLosTresJugadoresYSeIntentaCompletar() {
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
