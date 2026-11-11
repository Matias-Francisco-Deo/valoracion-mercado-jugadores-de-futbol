package com.overcode.persistence.repository;

import com.overcode.model.bonus.UserDailyBonus;
import com.overcode.model.user.User;
import com.overcode.persistence.repository.interfaces.UserDailyBonusRepository;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.testUtils.TestService;
import com.overcode.testUtils.TestUserUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class UserDailyBonusRepositoryTest {

    @Autowired
    private UserDailyBonusRepository userDailyBonusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestService testService;

    @BeforeEach
    void setUp() {
        testService.eliminarDailyBonuses();
        testService.eliminarUsuarios();
    }

    @AfterEach
    void tearDown() {
        testService.eliminarDailyBonuses();
        testService.eliminarUsuarios();
    }

    @Test
    @Transactional
    void alGuardarUserSePersisteYSeRecuperaPorEseUsuario() {
        User user = userRepository.guardar(TestUserUtil.getUsuarioBase("juan"));

        Optional<UserDailyBonus> recuperado = userDailyBonusRepository.recuperarPorUserId(user.getId());
        assertTrue(recuperado.isPresent());
        assertEquals(user.getUserDailyBonus().getId(), recuperado.get().getId());
    }

    @Test
    @Transactional
    void alGuardarUserSePersisteYSeRecuperaPorId() {
        User user = userRepository.guardar(TestUserUtil.getUsuarioBase("juan"));

        Optional<UserDailyBonus> recuperado = userDailyBonusRepository.recuperarPorId(user.getUserDailyBonus().getId());
        assertTrue(recuperado.isPresent());
        assertEquals(user.getUserDailyBonus().getId(), recuperado.get().getId());
    }

    @Test
    @Transactional
    void alBuscarPorIdInexistenteRetornaVacio() {
        Optional<UserDailyBonus> recuperado = userDailyBonusRepository.recuperarPorId(-1L);
        assertTrue(recuperado.isEmpty());
    }

    @Test
    @Transactional
    void alBuscarPorUsuarioInexistenteRetornaVacio() {
        Optional<UserDailyBonus> recuperado = userDailyBonusRepository.recuperarPorUserId(-1L);
        assertTrue(recuperado.isEmpty());
    }
}
