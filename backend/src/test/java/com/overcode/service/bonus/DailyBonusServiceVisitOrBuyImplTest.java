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
import com.overcode.model.player.Player;
import com.overcode.model.user.User;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import com.overcode.service.interfaces.ConfiguracionMercadoService;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest
class DailyBonusServiceVisitOrBuyImplTest {


    @Autowired
    private UserService userService;


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

}
