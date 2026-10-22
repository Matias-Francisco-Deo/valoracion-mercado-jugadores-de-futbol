package com.overcode.service;

import com.overcode.controller.dto.bonus.ClaimBonusResponseDTO;
import com.overcode.controller.dto.bonus.DailyBonusStatusResponseDTO;
import com.overcode.model.User;
import com.overcode.model.bonus.DiaDeLaSemana;
import com.overcode.model.bonus.LoginBonus;
import com.overcode.model.bonus.exception.BonusYaReclamadoHoyException;
import com.overcode.model.cotizacion.ConfiguracionCotizaciones;
import com.overcode.persistence.repository.interfaces.ConfiguracionCotizacionesRepository;
import com.overcode.persistence.repository.interfaces.LoginBonusRepository;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.interfaces.DailyBonusService;
import com.overcode.service.interfaces.UserService;
import com.overcode.testUtils.TestLoginBonusUtil;
import com.overcode.testUtils.TestService;
import com.overcode.testUtils.TestUserUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DailyBonusServiceImplTest {

    @Autowired
    private DailyBonusService dailyBonusService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginBonusRepository loginBonusRepository;

    @Autowired
    private ConfiguracionCotizacionesRepository configuracionCotizacionesRepository;

    @Autowired
    private TestService testService;

    private User usuarioPrueba;
    private LoginBonus bonusBase;

    @BeforeEach
    void setUp() {
        testService.eliminarUsuarios();
        testService.eliminarLoginBonuses();

        bonusBase = loginBonusRepository.guardar(TestLoginBonusUtil.getLoginBonusBase());

        ConfiguracionCotizaciones config = configuracionCotizacionesRepository.recuperarPorId(1L)
                .orElse(new ConfiguracionCotizaciones());
        config.setId(1L);
        config.setLoginBonus(bonusBase);
        configuracionCotizacionesRepository.guardar(config);

        usuarioPrueba = userService.guardar(TestUserUtil.getUsuarioDeNombre("matias"));
    }

    @AfterEach
    void tearDown() {
        testService.eliminarUsuarios();
        testService.eliminarLoginBonuses();
    }

    @Test
    void alReclamarBonusPorPrimeraVezSeOtorgaBonusLunesYPersisteRacha1() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        ClaimBonusResponseDTO respuesta = dailyBonusService.reclamarBonus("matias", hoy);

        assertNotNull(respuesta);
        assertEquals(1.0, respuesta.getCreditsAwarded());
        assertEquals(1, respuesta.getNewStreak());
        assertEquals(1.0, respuesta.getCurrentCreditBalance());
        assertEquals(hoy, respuesta.getClaimDate());

        User usuarioActualizado = userRepository.findByUsername("matias").orElseThrow();
        assertEquals(1.0, usuarioActualizado.getCreditBalance());
        assertEquals(1, usuarioActualizado.getStreakCount());
        assertEquals(hoy, usuarioActualizado.getLastBonusClaimDate());
    }

    @Test
    void alReclamarBonusEnDiasConsecutivosSeIncrementaLaRachaYElSaldo() {
        LocalDate dia1 = LocalDate.of(2026, 10, 21);
        LocalDate dia2 = LocalDate.of(2026, 10, 22);

        dailyBonusService.reclamarBonus("matias", dia1);
        ClaimBonusResponseDTO respuestaDia2 = dailyBonusService.reclamarBonus("matias", dia2);

        assertEquals(2.0, respuestaDia2.getCreditsAwarded());
        assertEquals(2, respuestaDia2.getNewStreak());
        assertEquals(3.0, respuestaDia2.getCurrentCreditBalance());
        assertEquals(dia2, respuestaDia2.getClaimDate());

        User usuarioActualizado = userRepository.findByUsername("matias").orElseThrow();
        assertEquals(3.0, usuarioActualizado.getCreditBalance());
        assertEquals(2, usuarioActualizado.getStreakCount());
    }

    @Test
    void alPasarMasDeUnDiaSinReclamarSeReiniciaLaRachaADia1() {
        LocalDate dia1 = LocalDate.of(2026, 10, 19);
        LocalDate dia3 = LocalDate.of(2026, 10, 22);

        dailyBonusService.reclamarBonus("matias", dia1);

        ClaimBonusResponseDTO respuesta = dailyBonusService.reclamarBonus("matias", dia3);
        assertEquals(1.0, respuesta.getCreditsAwarded());
        assertEquals(1, respuesta.getNewStreak());
        assertEquals(2.0, respuesta.getCurrentCreditBalance());
    }

    @Test
    void alCompletarCicloDe7DiasElSiguienteReclamoComienzaEnLunes() {
        LocalDate inicio = LocalDate.of(2026, 10, 1);
        for (int i = 0; i < 7; i++) {
            dailyBonusService.reclamarBonus("matias", inicio.plusDays(i));
        }

        User usuarioTras7Dias = userRepository.findByUsername("matias").orElseThrow();
        assertEquals(7, usuarioTras7Dias.getStreakCount());

        LocalDate dia8 = inicio.plusDays(7);
        ClaimBonusResponseDTO respuestaDia8 = dailyBonusService.reclamarBonus("matias", dia8);

        assertEquals(1.0, respuestaDia8.getCreditsAwarded());
        assertEquals(1, respuestaDia8.getNewStreak());
    }

    @Test
    void alIntentarReclamarDosVecesElMismoDiaLanzaBonusYaReclamadoHoyException() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        dailyBonusService.reclamarBonus("matias", hoy);

        assertThrows(BonusYaReclamadoHoyException.class, () ->
                dailyBonusService.reclamarBonus("matias", hoy)
        );
    }

    @Test
    void alConsultarEstadoAntesYDespuesDeReclamarReflejaElEstadoCorrecto() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        DailyBonusStatusResponseDTO estadoAntes = dailyBonusService.obtenerEstadoBonus("matias", hoy);
        assertEquals(0, estadoAntes.getCurrentStreak());
        assertEquals(1.0, estadoAntes.getTodayBonus());
        assertFalse(estadoAntes.getAlreadyClaimedToday());
        assertEquals(7, estadoAntes.getWeeklyBonuses().size());

        dailyBonusService.reclamarBonus("matias", hoy);

        DailyBonusStatusResponseDTO estadoDespues = dailyBonusService.obtenerEstadoBonus("matias", hoy);
        assertEquals(1, estadoDespues.getCurrentStreak());
        assertEquals(1.0, estadoDespues.getTodayBonus());
        assertTrue(estadoDespues.getAlreadyClaimedToday());
    }

    @Test
    void siUsuarioNoExisteLanzaEntidadNoEncontradaException() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);
        assertThrows(EntidadNoEncontradaException.class, () ->
                dailyBonusService.reclamarBonus("usuario_inexistente", hoy)
        );
    }
}
