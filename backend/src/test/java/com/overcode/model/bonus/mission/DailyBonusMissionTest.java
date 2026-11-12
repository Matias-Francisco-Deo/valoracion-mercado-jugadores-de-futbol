package com.overcode.model.bonus.mission;

import com.overcode.testUtils.bonus.mission.TestDailyBonusMission;
import com.overcode.testUtils.bonus.mission.TestMissionUtil;
import org.junit.jupiter.api.Test;

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
}
