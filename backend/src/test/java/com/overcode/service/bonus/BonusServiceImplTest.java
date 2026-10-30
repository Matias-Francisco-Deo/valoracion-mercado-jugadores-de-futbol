package com.overcode.service.bonus;

import com.overcode.model.bonus.BonusClaim;
import com.overcode.model.bonus.EstadoBonus;
import com.overcode.model.bonus.LoginBonus;
import com.overcode.model.cotizacion.ConfiguracionMercado;
import com.overcode.model.cotizacion.EstrategiaCotizacionRendimientoTotal;
import com.overcode.model.exception.bonus.BonusYaReclamadoHoyException;
import com.overcode.model.user.User;
import com.overcode.persistence.repository.interfaces.LoginBonusRepository;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.interfaces.BonusService;
import com.overcode.service.interfaces.ConfiguracionMercadoService;
import com.overcode.service.interfaces.UserService;
import com.overcode.testUtils.TestLoginBonusUtil;
import com.overcode.testUtils.TestService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.ZoneOffset;

import static com.overcode.testUtils.TestUserUtil.getUsuarioConRacha;
import static com.overcode.testUtils.TestUserUtil.getUsuarioDeNombre;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest
class BonusServiceImplTest {

    @Autowired
    private BonusService bonusService;

    @Autowired
    private UserService userService;

    @Autowired
    private LoginBonusRepository loginBonusRepository;

    @Autowired
    private TestService testService;

    private User USER_1;
    @Autowired
    private ConfiguracionMercadoService configuracionMercadoService;

    @BeforeEach
    void setUp() {
        testService.eliminarUsuarios();
        testService.eliminarLoginBonuses();
        testService.eliminarConfig();

        LoginBonus bonusBase = loginBonusRepository.guardar(TestLoginBonusUtil.getLoginBonusBase());

        ConfiguracionMercado config = new ConfiguracionMercado(1L, new EstrategiaCotizacionRendimientoTotal(), bonusBase);
        config.setId(1L);
        config.setLoginBonus(bonusBase);
        configuracionMercadoService.crearConfiguracionSiNoExiste(config);

        USER_1 = userService.guardar(getUsuarioConRacha("pepito", 0, null));
    }

    @AfterEach
    void tearDown() {
        testService.eliminarUsuarios();
        testService.eliminarLoginBonuses();
    }

    void mockedDateContext(LocalDate date, Runnable accion) {
        try (MockedStatic<LocalDate> dates = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
            dates.when(() -> LocalDate.now(ZoneOffset.UTC)).thenReturn(date);
            accion.run();

        }
    }

    @Test
    void alReclamarBonusPorPrimeraVezSeOtorgaPrimerBonusYPersisteRacha1() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        mockedDateContext(hoy, () -> {
            USER_1.setLastBonusClaimDate(null);
            BonusClaim respuesta = bonusService.reclamarBonus(USER_1);

            assertEquals(1.0, respuesta.givenBonus());
            assertEquals(1, respuesta.currentStreak());


            User usuarioActualizado = userService.recuperar(USER_1.getId());
            assertEquals(1.0, usuarioActualizado.getCreditBalance());
            assertEquals(1, usuarioActualizado.getStreakCount());
            assertEquals(hoy, usuarioActualizado.getLastBonusClaimDate());
        });
    }

    @Test
    void alReclamarBonusEnDiasConsecutivosSeIncrementaLaRachaYElSaldo() {
        LocalDate dia1 = LocalDate.of(2026, 10, 21);
        LocalDate dia2 = LocalDate.of(2026, 10, 22);

        mockedDateContext(dia1, () -> {
            USER_1.setLastBonusClaimDate(dia1);
            bonusService.reclamarBonus(USER_1);

        });

        mockedDateContext(dia2, () -> {
            USER_1.setLastBonusClaimDate(dia2);
            BonusClaim respuestaDia2 = bonusService.reclamarBonus(USER_1);

            assertEquals(2.0, respuestaDia2.givenBonus());
            assertEquals(2, respuestaDia2.currentStreak());
            assertEquals(3.0, respuestaDia2.currentCreditBalance());

            User usuarioActualizado = userService.recuperar(USER_1.getId());
            assertEquals(3.0, usuarioActualizado.getCreditBalance());
            assertEquals(2, usuarioActualizado.getStreakCount());
        });
    }

    @Test
    void alPasarMasDeUnDiaSinReclamarSeReiniciaLaRachaADia1() {
        LocalDate dia1 = LocalDate.of(2026, 10, 19);
        LocalDate dia3 = dia1.plusDays(2);

        mockedDateContext(dia1, () -> {
            USER_1.setLastBonusClaimDate(dia1);
            bonusService.reclamarBonus(USER_1);

        });

        mockedDateContext(dia3, () -> {
            USER_1.setLastBonusClaimDate(dia3);
            BonusClaim respuesta = bonusService.reclamarBonus(USER_1);
            assertEquals(1.0, respuesta.givenBonus());
            assertEquals(1, respuesta.currentStreak());
            assertEquals(2.0, respuesta.currentCreditBalance());
        });

    }

    @Test
    void alCompletarCicloDe7DiasElSiguienteReclamoVuelveLaRacha0() {
        LocalDate inicio = LocalDate.of(2026, 10, 1);
        LocalDate changingDate = null;
        for (int i = 0; i < 6; i++) {
            changingDate = inicio.plusDays(i);
            mockedDateContext(changingDate, () -> {
                bonusService.reclamarBonus(USER_1);
            });

        }

        User usuarioTras7Dias = userService.recuperar(USER_1.getId());

        assertEquals(6, usuarioTras7Dias.getStreakCount());

        LocalDate dia8 = changingDate.plusDays(1);
        mockedDateContext(dia8, () -> {
            BonusClaim respuestaDia8 = bonusService.reclamarBonus(USER_1);

            assertEquals(15.0, respuestaDia8.givenBonus());
            assertEquals(0, respuestaDia8.currentStreak());
        });

    }

    @Test
    void alIntentarReclamarDosVecesElMismoDiaLanzaBonusYaReclamadoHoyException() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);
        USER_1.setLastBonusClaimDate(hoy);
        bonusService.reclamarBonus(USER_1);

        assertThrows(BonusYaReclamadoHoyException.class, () ->
                bonusService.reclamarBonus(USER_1)
        );
    }

    @Test
    void alConsultarEstadoAntesYDespuesDeReclamarReflejaElEstadoCorrecto() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        mockedDateContext(hoy, () -> {
            EstadoBonus estadoAntes = bonusService.obtenerEstadoBonus(USER_1);
            assertEquals(0, estadoAntes.currentStreak());
            assertEquals(1.0, estadoAntes.nextBonus());
            assertFalse(estadoAntes.alreadyClaimedToday());

            bonusService.reclamarBonus(USER_1);

            EstadoBonus estadoDespues = bonusService.obtenerEstadoBonus(USER_1);
            assertEquals(1, estadoDespues.currentStreak());
            assertEquals(2.0, estadoDespues.nextBonus());
            assertTrue(estadoDespues.alreadyClaimedToday());
        });


    }

    @Test
    void siUsuarioNoExisteLanzaEntidadNoEncontradaException() {
        User usuarioInexistente = getUsuarioDeNombre("usuario_inexistente");
        usuarioInexistente.setId(-1L);
        assertThrows(EntidadNoEncontradaException.class, () ->
                bonusService.reclamarBonus(usuarioInexistente)
        );
    }
}
