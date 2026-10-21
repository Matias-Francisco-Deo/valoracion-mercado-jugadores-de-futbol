package com.overcode.service;

import com.overcode.model.cotizacion.EstrategiaCotizacion;
import com.overcode.model.cotizacion.registro.BusquedaRegistroCotizacion;
import com.overcode.model.cotizacion.registro.RegistroCotizacion;
import com.overcode.model.player.Player;
import com.overcode.model.player.Team;
import com.overcode.persistence.dto.jpa.cotizacion.TipoEstrategiaCotizacion;
import com.overcode.persistence.repository.impl.EstrategiaCotizacionRepositoryImpl;
import com.overcode.persistence.repository.impl.RegistroCotizacionRepositoryImpl;
import com.overcode.persistence.repository.impl.TeamRepositoryImpl;
import com.overcode.service.exception.RangoBusquedaInvalidoException;
import com.overcode.service.impl.RegistroCotizacionServiceImpl;
import com.overcode.testUtils.TestService;
import com.overcode.testUtils.cotizacion.siempre2.EstrategiaCotizacionSiempre2;
import com.overcode.testUtils.cotizacion.siempre2.EstrategiaCotizacionSiempre2MapAJPADTO;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.overcode.testUtils.TestPlayerUtil.getJugadorConLigaYClub;
import static com.overcode.testUtils.TestPlayerUtil.getJugadorConNombre;
import static com.overcode.testUtils.TestTeamUtil.getTeamConJugadores;
import static junit.framework.TestCase.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest()
class RegistroCotizacionServiceTest {

    public static final EstrategiaCotizacionSiempre2 ESTRATEGIA_COTIZACION_1 = new EstrategiaCotizacionSiempre2(10.0, 2.0);

    private final EstrategiaCotizacionSiempre2MapAJPADTO estrategiaADTO = new EstrategiaCotizacionSiempre2MapAJPADTO();

    @Autowired
    private EstrategiaCotizacionRepositoryImpl estrategiaCotizacionRepository;

    @Autowired
    private TeamRepositoryImpl teamRepository;

    @Autowired
    private TestService testService;

    @Autowired
    private RegistroCotizacionRepositoryImpl registroCotizacionRepositoryImpl;

    @Autowired
    private RegistroCotizacionServiceImpl registroCotizacionService;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @BeforeEach
    void setUp() {
        testService.eliminarJugadoresYEquipos();
        testService.eliminarCotizaciones();
        TipoEstrategiaCotizacion.agregarMapEstrategia(estrategiaADTO);
    }

    @AfterEach
    void tearDown() {
        testService.eliminarJugadoresYEquipos();
        testService.eliminarCotizaciones();
        TipoEstrategiaCotizacion.eliminarMapEstrategia(estrategiaADTO);
    }


    @Test
    @Transactional
    void guardarPersisteLosRegistros() {

        LocalDateTime startDate = LocalDateTime.now();

        Player jugadorGuardado = guardarRegistroDeJugador(getJugadorConNombre("pepito"));

        LocalDateTime endDate = LocalDateTime.now().plusSeconds(20);

        List<RegistroCotizacion> registrosRecuperados = registroCotizacionService.recuperarRegistrosDeJugador(
                new BusquedaRegistroCotizacion(jugadorGuardado.getId(), startDate, endDate)
        );

        assertFalse(registrosRecuperados.isEmpty());
        RegistroCotizacion registroRecuperado = registrosRecuperados.getFirst();

        assertNotNull(registroRecuperado.getId());
        assertEquals(10.0, registroRecuperado.getCotizacion());

        LocalDateTime createdAt = registrosRecuperados.getFirst().getCreatedAt();

        assertNotNull(createdAt);

        assertTrue(createdAt.isAfter(startDate));
        assertTrue(createdAt.isBefore(endDate));

    }

    @Test
    @Transactional
    void sePuedeBuscarPorRango() {

        LocalDateTime startDate = LocalDateTime.now();

        Player jugadorGuardado = guardarRegistroDeJugador(getJugadorConNombre("pepito"));

        LocalDateTime endDate = LocalDateTime.now();

        List<RegistroCotizacion> registrosRecuperados =
                registroCotizacionService.recuperarRegistrosDeJugador(
                        new BusquedaRegistroCotizacion(jugadorGuardado.getId(), startDate, endDate)
                );

        assertFalse(registrosRecuperados.isEmpty());
        RegistroCotizacion registroRecuperado = registrosRecuperados.getFirst();
        assertEquals(10.0, registroRecuperado.getCotizacion());

    }

    @Test
    @Transactional
    void sePuedeBuscarPorRangoAmplio() {

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now();

        Player jugadorGuardado = guardarRegistrosDeJugadorEnFechas(getJugadorConNombre("pepito"),
                startDate.minusDays(15),
                startDate.plusDays(5),
                startDate.plusDays(15));


        List<RegistroCotizacion> registrosRecuperados =
                registroCotizacionService.recuperarRegistrosDeJugador(
                        new BusquedaRegistroCotizacion(jugadorGuardado.getId(),
                                startDate.minusDays(30),
                                endDate.plusDays(30))
                );

        assertEquals(3, registrosRecuperados.size());

    }

    @Test
    @Transactional
    void seBuscaEnRangoQueNoHayNadaEntoncesEsVacio() {

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now();

        Player jugadorGuardado = guardarRegistrosDeJugadorEnFechas(getJugadorConNombre("pepito"),
                startDate.minusDays(15),
                startDate.plusDays(5),
                startDate.plusDays(15));


        List<RegistroCotizacion> registrosRecuperados =
                registroCotizacionService.recuperarRegistrosDeJugador(
                        new BusquedaRegistroCotizacion(jugadorGuardado.getId(), startDate, endDate)
                );

        assertTrue(registrosRecuperados.isEmpty());

    }

    @Test
    @Transactional
    void seBuscaSobreJugadorSinRegistrosYDaVacio() {

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now();

        guardarRegistrosDeJugadorEnFechas(getJugadorConNombre("pepito"),
                startDate,
                startDate,
                startDate);

        List<RegistroCotizacion> registrosRecuperados =
                registroCotizacionService.recuperarRegistrosDeJugador(
                        new BusquedaRegistroCotizacion(-1L, startDate, endDate)
                );

        assertTrue(registrosRecuperados.isEmpty());

    }

    @Test
    @Transactional
    void seBuscaSobreJugadorYDaRegistrosSoloDeEseJugador() {

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now();

        Player jugador1 = guardarRegistrosDeJugadorEnFechas(getJugadorConNombre("pepito"),
                startDate,
                startDate,
                startDate);

        guardarRegistrosDeJugadorEnFechas(getJugadorConLigaYClub("pepito2", "liga2", "club3"),
                startDate,
                startDate,
                startDate);

        List<RegistroCotizacion> registrosRecuperados =
                registroCotizacionService.recuperarRegistrosDeJugador(
                        new BusquedaRegistroCotizacion(jugador1.getId(), startDate, endDate)
                );

        registrosRecuperados.forEach(registroCotizacion ->
                assertEquals("pepito", registroCotizacion.getPlayer().getName()));

    }

    @Test
    @Transactional
    void seBuscaConRangosMalFormadosYDaError() {

        BusquedaRegistroCotizacion busquedaRegistroCotizacion = new BusquedaRegistroCotizacion(1L,
                LocalDateTime.now().plusDays(20), // este es más nuevo
                LocalDateTime.now()); // este es más viejo

        assertThrows(RangoBusquedaInvalidoException.class, () -> registroCotizacionService.recuperarRegistrosDeJugador(busquedaRegistroCotizacion));

    }

    @Test
    @Transactional
    void sePuedeBuscarPorRangoNuloYTraeTodo() {

        LocalDateTime startDate = LocalDateTime.now();

        Player jugadorGuardado = guardarRegistrosDeJugadorEnFechas(getJugadorConNombre("pepito"),
                startDate.minusDays(15),
                startDate.plusDays(5),
                startDate.plusDays(15));


        List<RegistroCotizacion> registrosRecuperados =
                registroCotizacionService.recuperarRegistrosDeJugador(
                        new BusquedaRegistroCotizacion(jugadorGuardado.getId(),
                                null,
                                null)
                );

        assertEquals(3, registrosRecuperados.size());

    }

    @Test
    @Transactional
    void sePuedeBuscarPorRangoNuloDeInicioYTraeTodoHastaFechaFin() {

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now();

        Player jugadorGuardado = guardarRegistrosDeJugadorEnFechas(getJugadorConNombre("pepito"),
                startDate.minusDays(15),
                startDate.plusDays(5),
                startDate.plusDays(15));


        List<RegistroCotizacion> registrosRecuperados =
                registroCotizacionService.recuperarRegistrosDeJugador(
                        new BusquedaRegistroCotizacion(jugadorGuardado.getId(),
                                null,
                                endDate.plusDays(6))
                );

        assertEquals(2, registrosRecuperados.size());

    }

    @Test
    @Transactional
    void sePuedeBuscarPorRangoNuloDeFinalYTraeTodoDesdePrimerFecha() {

        LocalDateTime startDate = LocalDateTime.now();

        Player jugadorGuardado = guardarRegistrosDeJugadorEnFechas(getJugadorConNombre("pepito"),
                startDate.minusDays(15),
                startDate.plusDays(5),
                startDate.plusDays(15));


        List<RegistroCotizacion> registrosRecuperados =
                registroCotizacionService.recuperarRegistrosDeJugador(
                        new BusquedaRegistroCotizacion(jugadorGuardado.getId(),
                                startDate.plusDays(10),
                                null)
                );

        assertEquals(1, registrosRecuperados.size());

    }

    private Player guardarRegistroDeJugador(Player player) {
        Team team1 = getTeamConJugadores(List.of(player));
        EstrategiaCotizacion estrategiaCotizacionUsada = estrategiaCotizacionRepository.guardar(ESTRATEGIA_COTIZACION_1);
        Team teamGuardado = teamRepository.guardar(team1);

        Player jugadorGuardado = teamGuardado.getPlayers().getFirst();
        List<RegistroCotizacion> registros = List.of(getRegistroCotizacion(jugadorGuardado, estrategiaCotizacionUsada));

        registroCotizacionRepositoryImpl.guardarTodos(registros);
        return jugadorGuardado;
    }

    private Player guardarRegistrosDeJugadorEnFechas(Player player,
                                                     LocalDateTime primerFecha,
                                                     LocalDateTime segundaFecha,
                                                     LocalDateTime tercerFecha) {
        Team team1 = player.getTeam();
        team1.addPlayer(player);
        EstrategiaCotizacion estrategiaCotizacionUsada = estrategiaCotizacionRepository.guardar(ESTRATEGIA_COTIZACION_1);
        Team teamGuardado = teamRepository.guardar(team1);

        Player jugadorGuardado = teamGuardado.getPlayers().getFirst();
        List<RegistroCotizacion> registros = List.of(
                getRegistroCotizacionConFecha(jugadorGuardado, estrategiaCotizacionUsada, primerFecha),
                getRegistroCotizacionConFecha(jugadorGuardado, estrategiaCotizacionUsada, segundaFecha),
                getRegistroCotizacionConFecha(jugadorGuardado, estrategiaCotizacionUsada, tercerFecha)
        );

        List<RegistroCotizacion> registrosGuardados = registroCotizacionRepositoryImpl.guardarTodos(registros);
        List<LocalDateTime> fechas = List.of(primerFecha, segundaFecha, tercerFecha);
        for (int i = 0; i < registrosGuardados.size(); i++) {
            jdbcTemplate.update(
                    "UPDATE registros_cotizaciones SET created_at = ? WHERE id = ?",
                    fechas.get(i),
                    registrosGuardados.get(i).getId()
            );
        }
        return jugadorGuardado;
    }

    private static @NonNull RegistroCotizacion getRegistroCotizacionConFecha(Player jugador1,
                                                                              EstrategiaCotizacion estrategiaCotizacionUsada,
                                                                              LocalDateTime creacion
    ) {
        RegistroCotizacion registroCotizacion = new RegistroCotizacion(
                jugador1,
                10.0,
                estrategiaCotizacionUsada
        );
        registroCotizacion.setCreatedAt(creacion);
        return registroCotizacion;
    }

    private static @NonNull RegistroCotizacion getRegistroCotizacion(Player jugador1, EstrategiaCotizacion estrategiaCotizacionUsada) {
        return new RegistroCotizacion(
                jugador1,
                10.0,
                estrategiaCotizacionUsada
        );
    }


}
