package com.overcode.persistence.repository;

import com.overcode.model.bonus.UserDailyBonus;
import com.overcode.model.user.User;
import com.overcode.persistence.repository.interfaces.UserDiaryBonusRepository;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.testUtils.TestService;
import com.overcode.testUtils.TestUserUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserDailyBonusRepositoryTest {

    @Autowired
    private UserDiaryBonusRepository userDiaryBonusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestService testService;

    @BeforeEach
    void setUp() {
        testService.eliminarDiaryBonuses();
        testService.eliminarUsuarios();
    }

    @AfterEach
    void tearDown() {
        testService.eliminarDiaryBonuses();
        testService.eliminarUsuarios();
    }

    @Test
    @Transactional
    void alGuardarUserDiaryBonusSePersisteYSeRecuperaPorUsuario() {
        User user = userRepository.guardar(TestUserUtil.getUsuarioBase("juan"));
        UserDailyBonus bonus = new UserDailyBonus(user);
        bonus.setStreakCount(3);
        bonus.setLastBonusClaimDate(LocalDate.of(2026, 11, 5));

        UserDailyBonus guardado = userDiaryBonusRepository.guardar(bonus);
        assertNotNull(guardado.getId());

        Optional<UserDailyBonus> recuperado = userDiaryBonusRepository.recuperarPorUsuario(user);
        assertTrue(recuperado.isPresent());
        assertEquals(3, recuperado.get().getStreakCount());
        assertEquals(LocalDate.of(2026, 11, 5), recuperado.get().getLastBonusClaimDate());
        assertEquals(user.getId(), recuperado.get().getUser().getId());
    }

    @Test
    @Transactional
    void alBuscarPorUsuarioInexistenteRetornaVacio() {
        User user = TestUserUtil.getUsuarioBase("fantasma");
        user.setId(999999L);

        Optional<UserDailyBonus> recuperado = userDiaryBonusRepository.recuperarPorUsuario(user);
        assertTrue(recuperado.isEmpty());
    }
}
