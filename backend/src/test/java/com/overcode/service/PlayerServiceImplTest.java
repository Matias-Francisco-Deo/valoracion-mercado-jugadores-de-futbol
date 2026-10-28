package com.overcode.service;

import com.overcode.controller.dto.player.PlayerFilter;
import com.overcode.model.cotizacion.ConfiguracionCotizaciones;
import com.overcode.model.player.Player;
import com.overcode.model.player.PlayerGameData;
import com.overcode.model.player.PlayerPosition;
import com.overcode.persistence.dto.jpa.cotizacion.mapper.TipoEstrategiaCotizacion;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.exception.NombreRepetidoException;
import com.overcode.service.interfaces.ConfiguracionCotizacionesService;
import com.overcode.service.interfaces.EstrategiaCotizacionService;
import com.overcode.service.interfaces.PlayerService;
import com.overcode.service.interfaces.TokenEmissionService;
import com.overcode.testUtils.TestService;
import com.overcode.testUtils.cotizacion.rating.EstrategiaCotizacionTestRatingMapAJPADTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.overcode.testUtils.TestPlayerUtil.*;
import static com.overcode.testUtils.cotizacion.TestEstrategiaCotizacionUtil.getEstrategiaRating;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest()
class PlayerServiceImplTest {

    @MockitoBean
    private TokenEmissionService tokenEmissionService;

    @Autowired
    private PlayerService playerService;

    private Player jugador1 = getJugadorConNombre("Messi");
    private Player jugador2 = getJugadorConNombre("Mbappe");

    @Autowired
    private TestService testService;
    @Autowired
    private EstrategiaCotizacionService estrategiaCotizacionService;

    private final EstrategiaCotizacionTestRatingMapAJPADTO estrategiaADTO = new EstrategiaCotizacionTestRatingMapAJPADTO();
    @Autowired
    private ConfiguracionCotizacionesService configuracionCotizacionesService;

    @BeforeEach
    void setUp() {
        testService.eliminarCotizaciones();
        testService.eliminarJugadoresYEquipos();
        TipoEstrategiaCotizacion.agregarMapEstrategia(estrategiaADTO);
        configuracionCotizacionesService.crearConfiguracionSiNoExiste(
                new ConfiguracionCotizaciones(getEstrategiaRating())
        );
    }

    @Test
    void crearJugadorValidoExitosamente() {
        Player nuevo = getJugadorConNombre("Messi");

        Player guardado = playerService.crear(nuevo);

        assertNotNull(guardado.getId());
        assertEquals("Messi", guardado.getName());
        assertEquals(1, guardado.getCurrentPrice());
        assertEquals(100, guardado.getTokens().size());
    }

    @Test
    void jugadorNuevoTiene100TokensYValeExactamente1() {
        Player nuevo = getJugadorConNombre("Messi");

        Player guardado = playerService.crear(nuevo);

        assertEquals(1, guardado.getCurrentPrice());
        assertEquals(100, guardado.getTokens().size());
    }

    @Test
    void rechazaCreacionConNombreDuplicado() {
        playerService.crear(jugador1);

        NombreRepetidoException exception = assertThrows(NombreRepetidoException.class,
            () -> playerService.crear(jugador1));

        assertTrue(exception.getMessage().contains("Messi"));
    }



    @Test
    void obtieneJugadorPorId() {
        Player creado = playerService.crear(jugador1);

        Player encontrado = playerService.recuperar(creado.getId());

        assertEquals(creado.getId(), encontrado.getId());
        assertEquals("Messi", encontrado.getName());
    }

    @Test
    void lanzaNotFoundCuandoNoExisteJugador() {
        assertThrows(EntidadNoEncontradaException.class, () -> playerService.recuperar(-1L));
    }

    @Test
    void listaTodosLosJugadores() {
        playerService.crear(jugador1);
        playerService.crear(jugador2);

        List<Player> jugadores = playerService.recuperarTodos();

        assertEquals(2, jugadores.size());
        assertTrue(jugadores.stream().anyMatch(player -> player.getName().equals("Messi")));
        assertTrue(jugadores.stream().anyMatch(player -> player.getName().equals("Mbappe")));
    }

    @Test
    void listaVaciaCuandoNoHayJugadores() {
        assertTrue(playerService.recuperarTodos().isEmpty());
    }

    @Test
    void listaRankingDeJugadoresPorEstrategiaActiva() {
        // usa estrategia que se basa en el rating
        Player jugadorUltimo = getJugadorConRating("Jugador1", 2D);
        playerService.crear(jugadorUltimo);

        Player jugadorAnteultimo = getJugadorConRating("Jugador2", 3D);
        playerService.crear(jugadorAnteultimo);

        Player jugador3 = getJugadorConRating("Jugador3", 4D);
        playerService.crear(jugador3);

        Player jugador4 = getJugadorConRating("Jugador4", 8D);
        playerService.crear(jugador4);

        Player jugador5 = getJugadorConRating("Jugador5", 9.5D);
        playerService.crear(jugador5);

        estrategiaCotizacionService.cotizarJugadores();

        List<Player> jugadores = playerService.recuperarRankingDeJugadoresPorEstrategiaActiva(
                org.springframework.data.domain.PageRequest.of(0, 10)).getContent();
        List<Player> expectedPlayers = List.of(jugador5, jugador4, jugador3, jugadorAnteultimo, jugadorUltimo);


        assertEquals(expectedPlayers.size(), jugadores.size());
        for (int i = 0; i < jugadores.size(); i++) {
            assertEquals(jugadores.get(i).getName(), expectedPlayers.get(i).getName());
        }

    }

    @Test
    void listarJugadoresRankingCuandoNoHayJugadoresDaVacio() {

        List<Player> jugadores = playerService.recuperarRankingDeJugadoresPorEstrategiaActiva(
                org.springframework.data.domain.PageRequest.of(0, 10)).getContent();

        assertTrue(jugadores.isEmpty());

    }

    @Test
    void listarJugadoresPorClub() {
        jugador1 = getJugadorConClub("Jugador1", "Club1");
        playerService.crear(jugador1);

        jugador2 = getJugadorConClub("Jugador2", "Club1");
        playerService.crear(jugador2);

        Player jugador3 = getJugadorConClub("Jugador3", "Club2");
        playerService.crear(jugador3);

        Player jugador4 = getJugadorConClub("Jugador4", "Club2");
        playerService.crear(jugador4);

        Player jugador5 = getJugadorConClub("Jugador5", "Club2");
        playerService.crear(jugador5);

        Page<Player> jugadores = playerService.recuperarTodosConFiltro(PlayerFilter.withClubname("Club1"),PageRequest.of(0, 10));

        List<Player> expectedPlayers = List.of(jugador1, jugador2);

        assertEquals(jugadores.getContent().size(), expectedPlayers.size());
        for (int i = 0; i < jugadores.getContent().size(); i++) {
            assertEquals(jugadores.getContent().get(i).getName(), expectedPlayers.get(i).getName());
        }

    }

    @Test
    void listarJugadoresPorLiga() {
        jugador1 = getJugadorConLigaYClub("Jugador1", "Liga1", "Club1");
        playerService.crear(jugador1);

        jugador2 = getJugadorConLigaYClub("Jugador2", "Liga1", "Club1");
        playerService.crear(jugador2);

        Player jugador3 = getJugadorConLigaYClub("Jugador3", "Liga2", "Club2");
        playerService.crear(jugador3);

        Player jugador4 = getJugadorConLigaYClub("Jugador4", "Liga2", "Club2");
        playerService.crear(jugador4);

        Player jugador5 = getJugadorConLigaYClub("Jugador5", "Liga2", "Club2");
        playerService.crear(jugador5);

        Page<Player> jugadores = playerService.recuperarTodosConFiltro(PlayerFilter.withLeague("Liga1"),PageRequest.of(0, 10));
        List<Player> expectedPlayers = List.of(jugador1, jugador2);


        assertEquals(jugadores.getContent().size(), expectedPlayers.size());
        for (int i = 0; i < jugadores.getContent().size(); i++) {
            assertEquals(jugadores.getContent().get(i).getName(), expectedPlayers.get(i).getName());
        }

    }

    @Test
    void listarJugadoresPorLigaYClub() {
        jugador1 = getJugadorConLigaYClub("Jugador1", "Liga1", "Club1");
        playerService.crear(jugador1);

        jugador2 = getJugadorConLigaYClub("Jugador2", "Liga1", "Club4");
        playerService.crear(jugador2);

        Player jugador3 = getJugadorConLigaYClub("Jugador3", "Liga2", "Club2");
        playerService.crear(jugador3);

        Player jugador4 = getJugadorConLigaYClub("Jugador4", "Liga2", "Club2");
        playerService.crear(jugador4);

        Player jugador5 = getJugadorConLigaYClub("Jugador5", "Liga2", "Club3");
        playerService.crear(jugador5);

        Page<Player> jugadores = playerService.recuperarTodosConFiltro(new PlayerFilter("Club1", "Liga1",null),PageRequest.of(0, 10));
        List<Player> expectedPlayers = List.of(jugador1);


        assertEquals(jugadores.getContent().size(), expectedPlayers.size());
        for (int i = 0; i < jugadores.getContent().size(); i++) {
            assertEquals(jugadores.getContent().get(i).getName(), expectedPlayers.get(i).getName());
        }

    }


    @Test
    void listarJugadoresPorPosicion() {
        Player defensor = getJugadorConPosicion("Defensor", PlayerPosition.DEFENSOR);
        playerService.crear(defensor);
        playerService.crear(getJugadorConPosicion("Atacante", PlayerPosition.ATACANTE));

        Page<Player> jugadores = playerService.recuperarTodosConFiltro(
                PlayerFilter.withPosition("DEFENSOR"), PageRequest.of(0, 10));

        assertEquals(1, jugadores.getContent().size());
        assertEquals("Defensor", jugadores.getContent().getFirst().getName());
    }

    @Test
    void listarJugadoresConFiltroLigaSinJugadoresDaVacio() {
        Page<Player> jugadores = playerService.recuperarTodosConFiltro(PlayerFilter.withLeague("Liga1"),PageRequest.of(0, 10));
        assertTrue(jugadores.isEmpty());
        assertEquals(0, jugadores.getTotalElements());
        assertEquals(0, jugadores.getTotalPages());

    }

    @Test
    void listarJugadoresConFiltroClubSinJugadoresDaVacio() {
        Page<Player> jugadores = playerService.recuperarTodosConFiltro(PlayerFilter.withClubname("Club1"),PageRequest.of(0, 10));
        assertTrue(jugadores.isEmpty());
        assertEquals(0, jugadores.getTotalElements());
        assertEquals(0, jugadores.getTotalPages());

    }

    private Player getJugadorConPosicion(String name, PlayerPosition position) {
        Player player = getJugadorConNombre(name);
        player.setPlayerGameData(new PlayerGameData(null, player, 0, 0, 0, 0, 0, 0, 1.0, position));
        return player;
    }

    @AfterEach
    void tearDown() {
        TipoEstrategiaCotizacion.eliminarMapEstrategia(estrategiaADTO);
        testService.eliminarCotizaciones();
        testService.eliminarJugadoresYEquipos();
    }
}
