package com.overcode.service.bonus;

import com.overcode.model.bonus.BonusClaim;
import com.overcode.model.bonus.DailyBonus;
import com.overcode.model.bonus.EstadoBonus;
import com.overcode.model.bonus.PeticionVisitaJugador;
import com.overcode.model.bonus.mission.BuyDailyBonusMission;
import com.overcode.model.bonus.mission.DailyBonusOrDailyBonusMission;
import com.overcode.model.bonus.mission.Visit3PlayersDailyBonusMission;
import com.overcode.model.bonus.mission.VisitPlayerDailyBonusMission;
import com.overcode.model.cotizacion.ConfiguracionMercado;
import com.overcode.model.cotizacion.EstrategiaCotizacionRendimientoTotal;
import com.overcode.model.exception.bonus.BonusYaReclamadoHoyException;
import com.overcode.model.exception.bonus.RequisitosMisionesNoCumplidosException;
import com.overcode.model.player.Player;
import com.overcode.model.user.User;
import com.overcode.persistence.repository.interfaces.DailyBonusRepository;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.interfaces.ConfiguracionMercadoService;
import com.overcode.service.interfaces.DailyBonusService;
import com.overcode.service.interfaces.UserDailyBonusService;
import com.overcode.service.interfaces.UserService;
import com.overcode.testUtils.TestDailyBonusUtil;
import com.overcode.testUtils.TestPlayerUtil;
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
class DailyBonusServiceImplTest {

    @Autowired
    private DailyBonusService dailyBonusService;

    @Autowired
    private UserService userService;

    @Autowired
    private DailyBonusRepository dailyBonusRepository;

    @Autowired
    private UserDailyBonusService userDailyBonusService;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TestService testService;

    @Autowired
    private ConfiguracionMercadoService configuracionMercadoService;

    private User USER_1;
    private Player PLAYER_1;
    private Player PLAYER_2;
    private Player PLAYER_3;

    @BeforeEach
    void setUp() {
        testService.eliminarUsuarios();
        testService.eliminarDiaryBonuses();
        testService.eliminarConfig();
        testService.eliminarJugadoresYEquipos();

        PLAYER_1 = playerRepository.guardar(TestPlayerUtil.crearJugador("Messi", 100.0));
        PLAYER_2 = playerRepository.guardar(TestPlayerUtil.crearJugador("Ronaldo", 90.0));
        PLAYER_3 = playerRepository.guardar(TestPlayerUtil.crearJugador("Neymar", 80.0));

        Visit3PlayersDailyBonusMission visitMission = new Visit3PlayersDailyBonusMission(
                new VisitPlayerDailyBonusMission(PLAYER_1),
                new VisitPlayerDailyBonusMission(PLAYER_2),
                new VisitPlayerDailyBonusMission(PLAYER_3)
        );
        BuyDailyBonusMission buyMission = new BuyDailyBonusMission();
        DailyBonusOrDailyBonusMission dailyOr = new DailyBonusOrDailyBonusMission(buyMission, visitMission);

        DailyBonus bonusBase = dailyBonusRepository.guardar(TestDailyBonusUtil.getDiaryBonusBase(dailyOr));

        ConfiguracionMercado config = new ConfiguracionMercado(1L, new EstrategiaCotizacionRendimientoTotal(), bonusBase);
        config.setId(1L);
        config.setDailyBonus(bonusBase);
        configuracionMercadoService.crearConfiguracionSiNoExiste(config);

        USER_1 = userService.guardar(getUsuarioConRacha("pepito", 0, null));
    }

    @AfterEach
    void tearDown() {
        testService.eliminarUsuarios();
        testService.eliminarDiaryBonuses();
        testService.eliminarJugadoresYEquipos();
    }

    void mockedDateContext(LocalDate date, Runnable accion) {
        try (MockedStatic<LocalDate> dates = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
            dates.when(() -> LocalDate.now(ZoneOffset.UTC)).thenReturn(date);
            accion.run();
        }
    }

    @Test
    void alCompletar3VisitasPermiteReclamarBonusCorrectamente() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        mockedDateContext(hoy, () -> {
            EstadoBonus e1 = dailyBonusService.registrarVisitaJugador(new PeticionVisitaJugador(USER_1, PLAYER_1));
            assertFalse(e1.puedeReclamar());

            EstadoBonus e2 = dailyBonusService.registrarVisitaJugador(new PeticionVisitaJugador(USER_1, PLAYER_2));
            assertFalse(e2.puedeReclamar());

            EstadoBonus e3 = dailyBonusService.registrarVisitaJugador(new PeticionVisitaJugador(USER_1, PLAYER_3));
            assertTrue(e3.puedeReclamar());

            BonusClaim respuesta = dailyBonusService.reclamarBonus(USER_1);
            assertEquals(1.0, respuesta.givenBonus());
            assertEquals(1, respuesta.currentStreak());

            User usuarioActualizado = userService.recuperar(USER_1.getId());
            assertEquals(1.0, usuarioActualizado.getCreditBalance());
            assertEquals(1, usuarioActualizado.getStreakCount());
            assertEquals(hoy, usuarioActualizado.getLastBonusClaimDate());
        });
    }

    @Test
    void alComprarTokenEnMercadoPermiteReclamarBonusSinVisitarJugadores() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        mockedDateContext(hoy, () -> {
            userDailyBonusService.registrarCompra(USER_1);

            EstadoBonus estado = dailyBonusService.obtenerEstadoBonus(USER_1);
            assertTrue(estado.puedeReclamar());

            BonusClaim claim = dailyBonusService.reclamarBonus(USER_1);
            assertEquals(1.0, claim.givenBonus());
            assertEquals(1, claim.currentStreak());
        });
    }

    @Test
    void alIntentarReclamarSinCumplirMisionesLanzaRequisitosMisionesNoCumplidosException() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        mockedDateContext(hoy, () -> {
            assertThrows(RequisitosMisionesNoCumplidosException.class, () ->
                    dailyBonusService.reclamarBonus(USER_1)
            );
        });
    }

    @Test
    void alReclamarBonusEnDiasConsecutivosSeIncrementaLaRachaYElSaldo() {
        LocalDate dia1 = LocalDate.of(2026, 10, 21);
        LocalDate dia2 = LocalDate.of(2026, 10, 22);

        mockedDateContext(dia1, () -> {
            userDailyBonusService.registrarCompra(USER_1);
            dailyBonusService.reclamarBonus(USER_1);
        });

        mockedDateContext(dia2, () -> {
            userDailyBonusService.registrarCompra(USER_1);
            BonusClaim respuestaDia2 = dailyBonusService.reclamarBonus(USER_1);

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
            userDailyBonusService.registrarCompra(USER_1);
            dailyBonusService.reclamarBonus(USER_1);
        });

        mockedDateContext(dia3, () -> {
            userDailyBonusService.registrarCompra(USER_1);
            BonusClaim respuesta = dailyBonusService.reclamarBonus(USER_1);
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
                userDailyBonusService.registrarCompra(USER_1);
                dailyBonusService.reclamarBonus(USER_1);
            });
        }

        User usuarioTras6Reclamos = userService.recuperar(USER_1.getId());
        assertEquals(6, usuarioTras6Reclamos.getStreakCount());

        LocalDate dia7 = changingDate.plusDays(1);
        mockedDateContext(dia7, () -> {
            userDailyBonusService.registrarCompra(USER_1);
            BonusClaim respuestaDia7 = dailyBonusService.reclamarBonus(USER_1);

            assertEquals(15.0, respuestaDia7.givenBonus());
            assertEquals(0, respuestaDia7.currentStreak());
        });
    }

    @Test
    void alIntentarReclamarDosVecesElMismoDiaLanzaBonusYaReclamadoHoyException() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);
        mockedDateContext(hoy, () -> {
            userDailyBonusService.registrarCompra(USER_1);
            dailyBonusService.reclamarBonus(USER_1);

            assertThrows(BonusYaReclamadoHoyException.class, () ->
                    dailyBonusService.reclamarBonus(USER_1)
            );
        });
    }

    @Test
    void alConsultarEstadoAntesYDespuesDeReclamarReflejaElEstadoCorrecto() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        mockedDateContext(hoy, () -> {
            EstadoBonus estadoAntes = dailyBonusService.obtenerEstadoBonus(USER_1);
            assertEquals(0, estadoAntes.currentStreak());
            assertEquals(1.0, estadoAntes.nextBonus());
            assertFalse(estadoAntes.alreadyClaimedToday());
            assertFalse(estadoAntes.puedeReclamar());
            assertNotNull(estadoAntes.misiones());
            assertFalse(estadoAntes.misiones().isEmpty());

            userDailyBonusService.registrarCompra(USER_1);
            EstadoBonus estadoConMisionCumplida = dailyBonusService.obtenerEstadoBonus(USER_1);
            assertTrue(estadoConMisionCumplida.puedeReclamar());

            dailyBonusService.reclamarBonus(USER_1);

            EstadoBonus estadoDespues = dailyBonusService.obtenerEstadoBonus(USER_1);
            assertEquals(1, estadoDespues.currentStreak());
            assertEquals(2.0, estadoDespues.nextBonus());
            assertTrue(estadoDespues.alreadyClaimedToday());
            assertFalse(estadoDespues.puedeReclamar());
        });
    }

    @Test
    void siUsuarioNoExisteLanzaEntidadNoEncontradaException() {
        User usuarioInexistente = getUsuarioDeNombre("usuario_inexistente");
        usuarioInexistente.setId(-1L);
        assertThrows(EntidadNoEncontradaException.class, () ->
                dailyBonusService.reclamarBonus(usuarioInexistente)
        );
    }
}
