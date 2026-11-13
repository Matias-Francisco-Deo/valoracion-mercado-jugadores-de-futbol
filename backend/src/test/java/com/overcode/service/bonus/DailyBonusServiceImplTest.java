package com.overcode.service.bonus;

import com.overcode.model.bonus.DailyBonus;
import com.overcode.model.bonus.mission.BuyDailyBonusMission;
import com.overcode.model.bonus.mission.BuyOrVisitMission;
import com.overcode.model.bonus.mission.DailyBonusMission;
import com.overcode.service.interfaces.DailyBonusService;
import com.overcode.service.interfaces.PlayerService;
import com.overcode.service.interfaces.UserService;
import com.overcode.testUtils.TestService;
import com.overcode.testUtils.bonus.mission.TestDailyBonusMission;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.overcode.testUtils.TestPlayerUtil.getJugadorConNombre;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
class DailyBonusServiceImplTest {

    @Autowired
    private UserService userService;


    @Autowired
    private TestService testService;

    @Autowired
    private DailyBonusService dailyBonusService;
    @Autowired
    private PlayerService playerService;

    @BeforeEach
    void setUp() {
        testService.eliminarUsuarios();
        testService.eliminarDailyBonuses();
        testService.eliminarConfig();
        testService.eliminarJugadoresYEquipos();

        testService.crearConfiguracionSimple();

    }


    @AfterEach
    void tearDown() {
        testService.eliminarUsuarios();
        testService.eliminarDailyBonuses();
        testService.eliminarConfig();
        testService.eliminarJugadoresYEquipos();
    }

    @Test
    void recuperarBonusActualDevuelveElBonusActual() {

        DailyBonus dailyBonus = dailyBonusService.recuperarSeleccionado();

        assertEquals(TestDailyBonusMission.class, dailyBonus.getGetRootMission().getClass());

    }

    @Test
    void crearNuevaMisionCreaBuySiNoHay3JugadoresMin() {
        assertEquals(BuyDailyBonusMission.class, dailyBonusService.crearNuevaMision().getClass());
    }

    @Test
    void crearNuevaMisionCreaBuyOrVisitSiHay3Jugadores() {

        userService.crearSuperusuario();

        playerService.crear(getJugadorConNombre("pepito1"));
        playerService.crear(getJugadorConNombre("pepito2"));
        playerService.crear(getJugadorConNombre("pepito3"));

        assertEquals(BuyOrVisitMission.class, dailyBonusService.crearNuevaMision().getClass());
    }

    @Test
    void crearNuevaMisionCreaBuyOrVisitSiHayMasDe3Jugadores() {
        userService.crearSuperusuario();

        playerService.crear(getJugadorConNombre("pepito1"));
        playerService.crear(getJugadorConNombre("pepito2"));
        playerService.crear(getJugadorConNombre("pepito3"));
        playerService.crear(getJugadorConNombre("pepito4"));

        assertEquals(BuyOrVisitMission.class, dailyBonusService.crearNuevaMision().getClass());
    }

    @Test
    void seleccionarMisionDejaEsaMisionEfectiva() {

        DailyBonus dailyBonus = dailyBonusService.recuperarSeleccionado();

        DailyBonusMission<?> newMission = dailyBonusService.seleccionarNuevaMision();

        DailyBonus dailyBonusConMisionNueva = dailyBonusService.recuperarSeleccionado();

        assertNotEquals(dailyBonus.getGetRootMission(), dailyBonusConMisionNueva.getGetRootMission());
        assertEquals(dailyBonusConMisionNueva.getGetRootMission().getId(), newMission.getId());

    }

}
