package com.overcode.persistence.repository;

import com.overcode.model.bonus.DiaryBonus;
import com.overcode.persistence.repository.interfaces.DiaryBonusRepository;
import com.overcode.testUtils.TestDiaryBonusUtil;
import com.overcode.testUtils.TestService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DiaryBonusRepositoryTest {

    @Autowired
    private DiaryBonusRepository diaryBonusRepository;

    @Autowired
    private TestService testService;

    @BeforeEach
    void setUp() {
        testService.eliminarDiaryBonuses();
    }

    @AfterEach
    void tearDown() {
        testService.eliminarDiaryBonuses();
    }

    @Test
    @Transactional
    void alGuardarUnDiaryBonusSePersisteCorrectamenteYSeRecuperaPorId() {
        DiaryBonus base = TestDiaryBonusUtil.getDiaryBonusBase();
        DiaryBonus guardado = diaryBonusRepository.guardar(base);

        assertNotNull(guardado.getId());
        assertEquals("Bonus Base Semanal", guardado.getNombre());
        assertEquals(1.0, guardado.getBonusConRacha(0));
        assertEquals(15.0, guardado.getBonusConRacha(6));

        Optional<DiaryBonus> recuperado = diaryBonusRepository.recuperarPorId(guardado.getId());
        assertTrue(recuperado.isPresent());
        assertEquals(guardado.getId(), recuperado.get().getId());
        assertEquals("Bonus Base Semanal", recuperado.get().getNombre());
        assertEquals(7, recuperado.get().getBonusesDeLaSemana().getRewards().size());
    }

    @Test
    @Transactional
    void alRecuperarPorNombreRetornaElDiaryBonusEsperado() {
        DiaryBonus base = TestDiaryBonusUtil.getDiaryBonusBase();
        diaryBonusRepository.guardar(base);

        Optional<DiaryBonus> recuperado = diaryBonusRepository.recuperarPorNombre("Bonus Base Semanal");
        assertTrue(recuperado.isPresent());
        assertEquals("Bonus Base Semanal", recuperado.get().getNombre());
    }

    @Test
    @Transactional
    void existePorNombreRetornaTrueSiExisteYFalseSiNoExiste() {
        assertFalse(diaryBonusRepository.existePorNombre("Bonus Inexistente"));

        DiaryBonus base = TestDiaryBonusUtil.getDiaryBonusBase();
        diaryBonusRepository.guardar(base);

        assertTrue(diaryBonusRepository.existePorNombre("Bonus Base Semanal"));
    }

    @Test
    @Transactional
    void alRecuperarTodosRetornaLaListaCompleta() {
        DiaryBonus base = TestDiaryBonusUtil.getDiaryBonusBase();
        diaryBonusRepository.guardar(base);

        List<DiaryBonus> todos = diaryBonusRepository.recuperarTodos();
        assertFalse(todos.isEmpty());
        assertEquals(1, todos.size());
    }
}
