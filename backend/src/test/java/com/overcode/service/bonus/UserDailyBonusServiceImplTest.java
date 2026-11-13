package com.overcode.service.bonus;

import com.overcode.model.bonus.BonusClaim;
import com.overcode.model.bonus.DailyBonus;
import com.overcode.model.bonus.EstadoBonus;
import com.overcode.model.exception.bonus.BonusYaReclamadoHoyException;
import com.overcode.model.exception.bonus.RequisitosMisionesNoCumplidosException;
import com.overcode.model.user.User;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.interfaces.DailyBonusService;
import com.overcode.service.interfaces.UserDailyBonusService;
import com.overcode.service.interfaces.UserService;
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
class UserDailyBonusServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDailyBonusService userDailyBonusService;

    @Autowired
    private TestService testService;


    private User USER_1;
    @Autowired
    private DailyBonusService dailyBonusService;

    @BeforeEach
    void setUp() {
        testService.eliminarUsuarios();
        testService.eliminarDailyBonuses();
        testService.eliminarConfig();
        testService.eliminarJugadoresYEquipos();

        testService.crearConfiguracionSimple();

        USER_1 = userService.crear(getUsuarioConRacha("pepito", 0, null));
    }


    @AfterEach
    void tearDown() {
        testService.eliminarUsuarios();
        testService.eliminarDailyBonuses();
        testService.eliminarConfig();
        testService.eliminarJugadoresYEquipos();
    }

    void completarMisionTest(User user) {
        DailyBonus dailyBonus = dailyBonusService.recuperarSeleccionado();
        User userRecuperado = userService.recuperar(user.getId());
        userRecuperado.getUserDailyBonus().actualizarMision(dailyBonus);

        userRecuperado.getUserDailyBonus().getActiveDailyBonusMission().complete(null); // completa la misión

        userDailyBonusService.guardar(userRecuperado.getUserDailyBonus());

    }

    void mockedDateContext(LocalDate date, Runnable accion) {
        try (MockedStatic<LocalDate> dates = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
            dates.when(() -> LocalDate.now(ZoneOffset.UTC)).thenReturn(date);
            accion.run();
        }
    }

    @Test
    void alIntentarReclamarSinCumplirMisionesLanzaRequisitosMisionesNoCumplidosException() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        mockedDateContext(hoy, () -> {
            assertThrows(RequisitosMisionesNoCumplidosException.class, () ->
                    userDailyBonusService.reclamarBonus(USER_1)
            );
        });
    }

    @Test
    void alReclamarBonusEnDiasConsecutivosSeIncrementaLaRachaYElSaldo() {
        LocalDate dia1 = LocalDate.of(2026, 10, 21);
        LocalDate dia2 = LocalDate.of(2026, 10, 22);


        mockedDateContext(dia1, () -> {
            completarMisionTest(USER_1);
            userDailyBonusService.reclamarBonus(USER_1);
        });

        mockedDateContext(dia2, () -> {
            completarMisionTest(USER_1);
            BonusClaim respuestaDia2 = userDailyBonusService.reclamarBonus(USER_1);

            assertEquals(2.0, respuestaDia2.givenBonus());
            assertEquals(2, respuestaDia2.currentStreak());
            assertEquals(3.0, respuestaDia2.currentCreditBalance());

            User usuarioActualizado = userService.recuperar(USER_1.getId());
            assertEquals(3.0, usuarioActualizado.getCreditBalance());
            assertEquals(2, usuarioActualizado.getUserDailyBonus().getStreakCount());
        });
    }

    @Test
    void alPasarMasDeUnDiaSinReclamarSeReiniciaLaRachaADia1() {
        LocalDate dia1 = LocalDate.of(2026, 10, 19);
        LocalDate dia3 = dia1.plusDays(2);

        mockedDateContext(dia1, () -> {
            completarMisionTest(USER_1);
            userDailyBonusService.reclamarBonus(USER_1);
        });

        mockedDateContext(dia3, () -> {
            completarMisionTest(USER_1);
            BonusClaim respuesta = userDailyBonusService.reclamarBonus(USER_1);
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
                completarMisionTest(USER_1);
                userDailyBonusService.reclamarBonus(USER_1);
            });
        }

        User usuarioTras6Reclamos = userService.recuperar(USER_1.getId());
        assertEquals(6, usuarioTras6Reclamos.getUserDailyBonus().getStreakCount());

        LocalDate dia7 = changingDate.plusDays(1);
        mockedDateContext(dia7, () -> {
            completarMisionTest(USER_1);
            BonusClaim respuestaDia7 = userDailyBonusService.reclamarBonus(USER_1);

            assertEquals(15.0, respuestaDia7.givenBonus());
            assertEquals(0, respuestaDia7.currentStreak());
        });
    }

    @Test
    void alIntentarReclamarDosVecesElMismoDiaLanzaBonusYaReclamadoHoyException() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);
        mockedDateContext(hoy, () -> {
            completarMisionTest(USER_1);
            userDailyBonusService.reclamarBonus(USER_1);

            assertThrows(BonusYaReclamadoHoyException.class, () ->
                    userDailyBonusService.reclamarBonus(USER_1)
            );
        });
    }

    @Test
    void alConsultarEstadoAntesDeCompletarMisionReflejaElEstadoCorrecto() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        mockedDateContext(hoy, () -> {
            EstadoBonus estadoAntes = userDailyBonusService.obtenerEstadoBonus(USER_1);
            assertEquals(0, estadoAntes.currentStreak());
            assertEquals(1.0, estadoAntes.nextBonus());
            assertFalse(estadoAntes.alreadyClaimedToday());
            assertFalse(estadoAntes.canClaimBonus());
        });
    }

    @Test
    void alConsultarEstadoLuegoDeCompletarMisionReflejaElEstadoCorrecto() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        mockedDateContext(hoy, () -> {
            completarMisionTest(USER_1);
            EstadoBonus estadoConMisionCumplida = userDailyBonusService.obtenerEstadoBonus(USER_1);
            assertTrue(estadoConMisionCumplida.canClaimBonus());


        });
    }

    @Test
    void alConsultarEstadoDespuesDeReclamarReflejaElEstadoCorrecto() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        mockedDateContext(hoy, () -> {
            completarMisionTest(USER_1);
            userDailyBonusService.reclamarBonus(USER_1);

            EstadoBonus estadoDespues = userDailyBonusService.obtenerEstadoBonus(USER_1);
            assertEquals(1, estadoDespues.currentStreak());
            assertEquals(2.0, estadoDespues.nextBonus());
            assertTrue(estadoDespues.alreadyClaimedToday());
            assertFalse(estadoDespues.canClaimBonus());
        });
    }

    @Test
    void siUsuarioNoExisteLanzaEntidadNoEncontradaException() {
        User usuarioInexistente = getUsuarioDeNombre("usuario_inexistente");
        usuarioInexistente.setId(-1L);
        assertThrows(EntidadNoEncontradaException.class, () ->
                userDailyBonusService.reclamarBonus(usuarioInexistente)
        );
    }
}
