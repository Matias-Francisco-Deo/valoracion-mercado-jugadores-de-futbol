package com.overcode.model.bonus.mission;

import com.overcode.testUtils.bonus.mission.TestDailyBonusMission;
import com.overcode.testUtils.bonus.mission.TestMissionUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DailyBonusMissionTest {

    @Test
    void alCompletarTestMissionCambiaSuEstadoACompletado() {
        TestDailyBonusMission mision = TestMissionUtil.crearTestMission("Mision de prueba");
        assertFalse(mision.isCompleted());

        mision.complete("contexto");
        assertTrue(mision.isCompleted());
    }

    @Test
    void clonarMisionGeneraCopiaIndependienteConMismoEstadoYSinId() {
        TestDailyBonusMission original = TestMissionUtil.crearTestMission("Original", true);
        TestDailyBonusMission clon = original.clonar();

        assertNotSame(original, clon);
        assertNull(clon.getId());
        assertEquals(original.getNombre(), clon.getNombre());
        assertEquals(original.isCompleted(), clon.isCompleted());
    }

    @Test
    void reduceCuentaLasHojasApropiadamente() {
        DailyBonusMission<?> mission1 = new TestDailyBonusMission();
        DailyBonusMission<?> mission2 = new TestDailyBonusMission();
        DailyBonusMission<?> mission3 = new TestDailyBonusMission();

        mission1.setSubmissions(List.of(mission2, mission3));

        int result = mission1.reduce(0, recMission -> recMission.getSubmissions().isEmpty() ? 1 : 0, Integer::sum);

        assertEquals(2, result);
    }

    @Test
    void forEachEjecutaAccionesSinResultado() {
        DailyBonusMission<?> mission1 = new TestDailyBonusMission();
        DailyBonusMission<?> mission2 = new TestDailyBonusMission();
        DailyBonusMission<?> mission3 = new TestDailyBonusMission();

        mission1.setSubmissions(List.of(mission2, mission3));

        mission1.forEach(DailyBonusMission::setComplete);

        assertTrue(mission1.isCompleted());
        assertTrue(mission2.isCompleted());
        assertTrue(mission3.isCompleted());
    }
}
