package com.overcode.model.bonus.mission;

import com.overcode.testUtils.TestMissionUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MissionTest {

    @Test
    void alCompletarTestMissionCambiaSuEstadoACompletado() {
        TestMission<String> mision = TestMissionUtil.crearTestMission("Mision de prueba");
        assertFalse(mision.isCompleted());

        mision.complete("contexto");
        assertTrue(mision.isCompleted());
    }

    @Test
    void orMissionRetornaFalseCuandoNingunaSubmisionEstaCompletada() {
        TestMission<String> m1 = TestMissionUtil.crearTestMission("Submision 1");
        TestMission<String> m2 = TestMissionUtil.crearTestMission("Submision 2");

        GenericOrMission<Object> orMission = TestMissionUtil.crearOrMission("OR Compuesta", m1, m2);
        assertFalse(orMission.isCompleted());
    }

    @Test
    void orMissionRetornaTrueCuandoAlMenosUnaSubmisionEstaCompletada() {
        TestMission<String> m1 = TestMissionUtil.crearTestMission("Submision 1");
        TestMission<String> m2 = TestMissionUtil.crearTestMission("Submision 2");

        GenericOrMission<Object> orMission = TestMissionUtil.crearOrMission("OR Compuesta", m1, m2);
        m1.complete("ok");

        assertTrue(orMission.isCompleted());
    }

    @Test
    void andMissionRetornaFalseCuandoAlgunaSubmisionNoEstaCompletada() {
        TestMission<String> m1 = TestMissionUtil.crearTestMission("Submision 1");
        TestMission<String> m2 = TestMissionUtil.crearTestMission("Submision 2");

        GenericAndMission<Object> andMission = TestMissionUtil.crearAndMission("AND Compuesta", m1, m2);
        m1.complete("ok");

        assertFalse(andMission.isCompleted());
    }

    @Test
    void andMissionRetornaTrueCuandoTodasLasSubmisionesEstanCompletadas() {
        TestMission<String> m1 = TestMissionUtil.crearTestMission("Submision 1");
        TestMission<String> m2 = TestMissionUtil.crearTestMission("Submision 2");

        GenericAndMission<Object> andMission = TestMissionUtil.crearAndMission("AND Compuesta", m1, m2);
        m1.complete("ok");
        m2.complete("ok");

        assertTrue(andMission.isCompleted());
    }

    @Test
    void andMissionRetornaFalseSiNoTieneSubmisiones() {
        GenericAndMission<Object> andMission = new GenericAndMission<>("AND vacia");
        assertFalse(andMission.isCompleted());
    }

    @Test
    void clonarMisionGeneraCopiaIndependienteConMismoEstado() {
        TestMission<String> original = TestMissionUtil.crearTestMission("Original", true);
        TestMission<String> clon = original.clonar();

        assertNotSame(original, clon);
        assertEquals(original.getNombre(), clon.getNombre());
        assertEquals(original.isCompleted(), clon.isCompleted());
    }
}
