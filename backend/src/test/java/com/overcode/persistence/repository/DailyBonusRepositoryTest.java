package com.overcode.persistence.repository;

import com.overcode.model.bonus.DailyBonus;
import com.overcode.persistence.repository.interfaces.DailyBonusRepository;
import com.overcode.testUtils.TestDailyBonusUtil;
import com.overcode.testUtils.TestService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.overcode.testUtils.TestService.agregarTestMissionMap;
import static com.overcode.testUtils.TestService.quitarTestMissionMap;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DailyBonusRepositoryTest {

    @Autowired
    private DailyBonusRepository dailyBonusRepository;

    @Autowired
    private TestService testService;

    @BeforeEach
    void setUp() {
        agregarTestMissionMap();
        testService.eliminarDailyBonuses();
    }

    @AfterEach
    void tearDown() {
        quitarTestMissionMap();
        testService.eliminarDailyBonuses();
    }

    @Test
    @Transactional
    void alGuardarUnDiaryBonusSePersisteCorrectamenteYSeRecuperaPorId() {
        DailyBonus base = TestDailyBonusUtil.getDailyBonusBase();
        DailyBonus guardado = dailyBonusRepository.guardar(base);

        assertNotNull(guardado.getId());
        assertEquals("Bonus Base Semanal", guardado.getNombre());
        assertEquals(1.0, guardado.getBonusConRacha(0));
        assertEquals(15.0, guardado.getBonusConRacha(6));

        Optional<DailyBonus> recuperado = dailyBonusRepository.recuperarPorId(guardado.getId());
        assertTrue(recuperado.isPresent());
        assertEquals(guardado.getId(), recuperado.get().getId());
        assertEquals("Bonus Base Semanal", recuperado.get().getNombre());
        assertEquals(7, recuperado.get().getBonusesDeLaSemana().getRewards().size());
    }

    @Test
    @Transactional
    void alRecuperarPorNombreRetornaElDiaryBonusEsperado() {
        DailyBonus base = TestDailyBonusUtil.getDailyBonusBase();
        dailyBonusRepository.guardar(base);

        Optional<DailyBonus> recuperado = dailyBonusRepository.recuperarPorNombre("Bonus Base Semanal");
        assertTrue(recuperado.isPresent());
        assertEquals("Bonus Base Semanal", recuperado.get().getNombre());
    }

    @Test
    @Transactional
    void existePorNombreRetornaTrueSiExisteYFalseSiNoExiste() {
        assertFalse(dailyBonusRepository.existePorNombre("Bonus Inexistente"));

        DailyBonus base = TestDailyBonusUtil.getDailyBonusBase();
        dailyBonusRepository.guardar(base);

        assertTrue(dailyBonusRepository.existePorNombre("Bonus Base Semanal"));
    }

    @Test
    @Transactional
    void alRecuperarTodosRetornaLaListaCompleta() {
        DailyBonus base = TestDailyBonusUtil.getDailyBonusBase();
        dailyBonusRepository.guardar(base);

        List<DailyBonus> todos = dailyBonusRepository.recuperarTodos();
        assertFalse(todos.isEmpty());
        assertEquals(1, todos.size());
    }
}
