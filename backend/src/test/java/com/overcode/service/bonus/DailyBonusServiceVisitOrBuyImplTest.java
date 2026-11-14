package com.overcode.service.bonus;

import com.overcode.model.bonus.BonusClaim;
import com.overcode.model.bonus.EstadoBonus;
import com.overcode.model.bonus.PeticionVisitaJugador;
import com.overcode.model.bonus.UserDailyBonus;
import com.overcode.model.cotizacion.ConfiguracionMercado;
import com.overcode.model.player.Player;
import com.overcode.model.user.User;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import com.overcode.service.interfaces.ConfiguracionMercadoService;
import com.overcode.service.interfaces.UserDailyBonusService;
import com.overcode.service.interfaces.UserService;
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
import static com.overcode.testUtils.bonus.TestBonusUtil.getConfigConDailyOr;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    private User user1;
    private Player player1;
    private Player player2;
    private Player player3;

    @BeforeEach
    void setUp() {
        testService.eliminarUsuarios();
        testService.eliminarDailyBonuses();
        testService.eliminarConfig();
        testService.eliminarJugadoresYEquipos();

        player1 = playerRepository.guardar(TestPlayerUtil.crearJugador("Messi", 100.0));
        player2 = playerRepository.guardar(TestPlayerUtil.crearJugador("Ronaldo", 90.0));
        player3 = playerRepository.guardar(TestPlayerUtil.crearJugador("Neymar", 80.0));

        ConfiguracionMercado config = getConfigConDailyOr(player1, player2, player3);

        configuracionMercadoService.crearConfiguracionSiNoExiste(config);

        user1 = userService.crear(getUsuarioConRacha("pepito", 0, null));
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
            userDailyBonusService.registrarVisitaJugador(new PeticionVisitaJugador(user1, player1));

            userDailyBonusService.registrarVisitaJugador(new PeticionVisitaJugador(user1, player2));

            userDailyBonusService.registrarVisitaJugador(new PeticionVisitaJugador(user1, player3));

            BonusClaim respuesta = userDailyBonusService.reclamarBonus(user1);
            assertEquals(1.0, respuesta.givenBonus());
            assertEquals(1, respuesta.currentStreak());

            User usuarioActualizado = userService.recuperar(user1.getId());
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
            userDailyBonusService.registrarCompra(user1);

            EstadoBonus estado = userDailyBonusService.obtenerEstadoBonus(user1);
            assertTrue(estado.canClaimBonus());

            BonusClaim claim = userDailyBonusService.reclamarBonus(user1);
            assertEquals(1.0, claim.givenBonus());
            assertEquals(1, claim.currentStreak());
        });
    }

}
