package com.overcode.model.bonus.mission;

import com.overcode.testUtils.TestMissionUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DailyBonusMissionTest {

    @Test
    void alCompletarTestMissionCambiaSuEstadoACompletado() {
        TestDailyBonusMission<String> mision = TestMissionUtil.crearTestMission("Mision de prueba");
        assertFalse(mision.isCompleted());

        mision.complete("contexto");
        assertTrue(mision.isCompleted());
    }

    @Test
    void orMissionRetornaFalseCuandoNingunaSubmisionEstaCompletada() {
        TestDailyBonusMission<String> m1 = TestMissionUtil.crearTestMission("Submision 1");
        TestDailyBonusMission<String> m2 = TestMissionUtil.crearTestMission("Submision 2");

        GenericOrDailyBonusMission<Object> orMission = TestMissionUtil.crearOrMission("OR Compuesta", m1, m2);
        assertFalse(orMission.isCompleted());
    }

    @Test
    void orMissionRetornaTrueCuandoAlMenosUnaSubmisionEstaCompletada() {
        TestDailyBonusMission<String> m1 = TestMissionUtil.crearTestMission("Submision 1");
        TestDailyBonusMission<String> m2 = TestMissionUtil.crearTestMission("Submision 2");

        GenericOrDailyBonusMission<Object> orMission = TestMissionUtil.crearOrMission("OR Compuesta", m1, m2);
        m1.complete("ok");

        assertTrue(orMission.isCompleted());
    }

    @Test
    void andMissionRetornaFalseCuandoAlgunaSubmisionNoEstaCompletada() {
        TestDailyBonusMission<String> m1 = TestMissionUtil.crearTestMission("Submision 1");
        TestDailyBonusMission<String> m2 = TestMissionUtil.crearTestMission("Submision 2");

        GenericAndDailyBonusMission<Object> andMission = TestMissionUtil.crearAndMission("AND Compuesta", m1, m2);
        m1.complete("ok");

        assertFalse(andMission.isCompleted());
    }

    @Test
    void andMissionRetornaTrueCuandoTodasLasSubmisionesEstanCompletadas() {
        TestDailyBonusMission<String> m1 = TestMissionUtil.crearTestMission("Submision 1");
        TestDailyBonusMission<String> m2 = TestMissionUtil.crearTestMission("Submision 2");

        GenericAndDailyBonusMission<Object> andMission = TestMissionUtil.crearAndMission("AND Compuesta", m1, m2);
        m1.complete("ok");
        m2.complete("ok");

        assertTrue(andMission.isCompleted());
    }

    @Test
    void andMissionRetornaFalseSiNoTieneSubmisiones() {
        GenericAndDailyBonusMission<Object> andMission = new GenericAndDailyBonusMission<>("AND vacia");
        assertFalse(andMission.isCompleted());
    }

    @Test
    void clonarMisionGeneraCopiaIndependienteConMismoEstado() {
        TestDailyBonusMission<String> original = TestMissionUtil.crearTestMission("Original", true);
        TestDailyBonusMission<String> clon = original.clonar();

        assertNotSame(original, clon);
        assertEquals(original.getNombre(), clon.getNombre());
        assertEquals(original.isCompleted(), clon.isCompleted());
    }
}
