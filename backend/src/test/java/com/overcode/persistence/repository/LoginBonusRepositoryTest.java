package com.overcode.persistence.repository;

import com.overcode.model.bonus.DiaDeLaSemana;
import com.overcode.model.bonus.LoginBonus;
import com.overcode.persistence.exception.LoginBonusPersistenciaException;
import com.overcode.persistence.repository.interfaces.LoginBonusRepository;
import com.overcode.testUtils.TestLoginBonusUtil;
import com.overcode.testUtils.TestService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoginBonusRepositoryTest {

    @Autowired
    private LoginBonusRepository loginBonusRepository;

    @Autowired
    private TestService testService;

    @BeforeEach
    void setUp() {
        testService.eliminarLoginBonuses();
    }

    @AfterEach
    void tearDown() {
        testService.eliminarLoginBonuses();
    }

    @Test
    void alGuardarUnLoginBonusSePersisteCorrectamenteYSeRecuperaPorId() {
        LoginBonus base = TestLoginBonusUtil.getLoginBonusBase();
        LoginBonus guardado = loginBonusRepository.guardar(base);

        assertNotNull(guardado.getId());
        assertEquals("Bonus Base Semanal", guardado.getNombre());
        assertEquals(1.0, guardado.getBonusParaDia(DiaDeLaSemana.LUNES));
        assertEquals(15.0, guardado.getBonusParaDia(DiaDeLaSemana.DOMINGO));

        Optional<LoginBonus> recuperado = loginBonusRepository.recuperarPorId(guardado.getId());
        assertTrue(recuperado.isPresent());
        assertEquals(guardado.getId(), recuperado.get().getId());
        assertEquals("Bonus Base Semanal", recuperado.get().getNombre());
        assertEquals(7, recuperado.get().getBonusesDeLaSemana().size());
    }

    @Test
    void alRecuperarPorNombreRetornaElLoginBonusEsperado() {
        LoginBonus base = TestLoginBonusUtil.getLoginBonusBase();
        loginBonusRepository.guardar(base);

        Optional<LoginBonus> recuperado = loginBonusRepository.recuperarPorNombre("Bonus Base Semanal");
        assertTrue(recuperado.isPresent());
        assertEquals("Bonus Base Semanal", recuperado.get().getNombre());
    }

    @Test
    void existePorNombreRetornaTrueSiExisteYFalseSiNoExiste() {
        assertFalse(loginBonusRepository.existePorNombre("Bonus Inexistente"));

        LoginBonus base = TestLoginBonusUtil.getLoginBonusBase();
        loginBonusRepository.guardar(base);

        assertTrue(loginBonusRepository.existePorNombre("Bonus Base Semanal"));
    }

    @Test
    void alGuardarLoginBonusNuloLanzaLoginBonusPersistenciaException() {
        assertThrows(LoginBonusPersistenciaException.class, () -> loginBonusRepository.guardar(null));
    }

    @Test
    void alRecuperarTodosRetornaLaListaCompleta() {
        LoginBonus base = TestLoginBonusUtil.getLoginBonusBase();
        loginBonusRepository.guardar(base);

        List<LoginBonus> todos = loginBonusRepository.recuperarTodos();
        assertFalse(todos.isEmpty());
        assertEquals(1, todos.size());
    }
}
