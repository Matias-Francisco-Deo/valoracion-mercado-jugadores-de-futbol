package com.overcode.service.bonus;

import com.overcode.model.bonus.BonusClaim;
import com.overcode.model.bonus.EstadoBonus;
import com.overcode.model.bonus.PeticionVisitaJugador;
import com.overcode.model.bonus.UserDailyBonus;
import com.overcode.model.bonus.mission.BuyDailyBonusMission;
import com.overcode.model.bonus.mission.BuyOrVisitMission;
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
import org.jspecify.annotations.NonNull;
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
        testService.eliminarDailyBonuses();
        testService.eliminarConfig();
        testService.eliminarJugadoresYEquipos();

        PLAYER_1 = playerRepository.guardar(TestPlayerUtil.crearJugador("Messi", 100.0));
        PLAYER_2 = playerRepository.guardar(TestPlayerUtil.crearJugador("Ronaldo", 90.0));
        PLAYER_3 = playerRepository.guardar(TestPlayerUtil.crearJugador("Neymar", 80.0));

        ConfiguracionMercado config = getConfigConDailyOr();

        configuracionMercadoService.crearConfiguracionSiNoExiste(config);

        USER_1 = userService.crear(getUsuarioConRacha("pepito", 0, null));
    }

    private @NonNull ConfiguracionMercado getConfigConDailyOr() {
        Visit3PlayersDailyBonusMission visitMission = new Visit3PlayersDailyBonusMission(
                new VisitPlayerDailyBonusMission(PLAYER_1),
                new VisitPlayerDailyBonusMission(PLAYER_2),
                new VisitPlayerDailyBonusMission(PLAYER_3)
        );
        BuyDailyBonusMission buyMission = new BuyDailyBonusMission();
        BuyOrVisitMission dailyOr = new BuyOrVisitMission(buyMission, visitMission);

        return new ConfiguracionMercado(new EstrategiaCotizacionRendimientoTotal(), TestDailyBonusUtil.getDailyBonusBaseConMision(dailyOr));
    }

    @AfterEach
    void tearDown() {
        testService.eliminarUsuarios();
        testService.eliminarDailyBonuses();
        testService.eliminarConfig();
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
            EstadoBonus e1 = userDailyBonusService.registrarVisitaJugador(new PeticionVisitaJugador(USER_1, PLAYER_1));
            assertFalse(e1.canClaimBonus());

            EstadoBonus e2 = userDailyBonusService.registrarVisitaJugador(new PeticionVisitaJugador(USER_1, PLAYER_2));
            assertFalse(e2.canClaimBonus());

            EstadoBonus e3 = userDailyBonusService.registrarVisitaJugador(new PeticionVisitaJugador(USER_1, PLAYER_3));
            assertTrue(e3.canClaimBonus());

            BonusClaim respuesta = userDailyBonusService.reclamarBonus(USER_1);
            assertEquals(1.0, respuesta.givenBonus());
            assertEquals(1, respuesta.currentStreak());

            User usuarioActualizado = userService.recuperar(USER_1.getId());
            assertEquals(1.0, usuarioActualizado.getCreditBalance());

            UserDailyBonus userDailyBonus = usuarioActualizado.getUserDailyBonus();

            assertEquals(1, userDailyBonus.getStreakCount());
            assertEquals(hoy, userDailyBonus.getLastBonusClaimDate());
        });
    }

    @Test
    void alComprarTokenEnMercadoPermiteReclamarBonusSinVisitarJugadores() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        mockedDateContext(hoy, () -> {
            userDailyBonusService.registrarCompra(USER_1);

            EstadoBonus estado = userDailyBonusService.obtenerEstadoBonus(USER_1);
            assertTrue(estado.canClaimBonus());

            BonusClaim claim = userDailyBonusService.reclamarBonus(USER_1);
            assertEquals(1.0, claim.givenBonus());
            assertEquals(1, claim.currentStreak());
        });
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

        // TODO cambiar por otra misión?

        mockedDateContext(dia1, () -> {
            userDailyBonusService.registrarCompra(USER_1);
            userDailyBonusService.reclamarBonus(USER_1);
        });

        mockedDateContext(dia2, () -> {
            userDailyBonusService.registrarCompra(USER_1);
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
            userDailyBonusService.registrarCompra(USER_1);
            userDailyBonusService.reclamarBonus(USER_1);
        });

        mockedDateContext(dia3, () -> {
            userDailyBonusService.registrarCompra(USER_1);
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
                userDailyBonusService.registrarCompra(USER_1);
                userDailyBonusService.reclamarBonus(USER_1);
            });
        }

        User usuarioTras6Reclamos = userService.recuperar(USER_1.getId());
        assertEquals(6, usuarioTras6Reclamos.getUserDailyBonus().getStreakCount());

        LocalDate dia7 = changingDate.plusDays(1);
        mockedDateContext(dia7, () -> {
            userDailyBonusService.registrarCompra(USER_1);
            BonusClaim respuestaDia7 = userDailyBonusService.reclamarBonus(USER_1);

            assertEquals(15.0, respuestaDia7.givenBonus());
            assertEquals(0, respuestaDia7.currentStreak());
        });
    }

    @Test
    void alIntentarReclamarDosVecesElMismoDiaLanzaBonusYaReclamadoHoyException() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);
        mockedDateContext(hoy, () -> {
            userDailyBonusService.registrarCompra(USER_1);
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
            userDailyBonusService.registrarCompra(USER_1);
            EstadoBonus estadoConMisionCumplida = userDailyBonusService.obtenerEstadoBonus(USER_1);
            assertTrue(estadoConMisionCumplida.canClaimBonus());

            userDailyBonusService.reclamarBonus(USER_1);
        });
    }

    @Test
    void alConsultarEstadoDespuesDeReclamarReflejaElEstadoCorrecto() {
        LocalDate hoy = LocalDate.of(2026, 10, 22);

        mockedDateContext(hoy, () -> {

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
