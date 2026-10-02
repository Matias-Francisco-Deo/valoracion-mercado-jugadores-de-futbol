package com.overcode.service;

import com.overcode.controller.dto.player.PlayerFilter;
import com.overcode.model.Player;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.exception.NombreRepetidoException;
import com.overcode.service.interfaces.PlayerService;
import com.overcode.testUtils.TestService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.overcode.testUtils.TestPlayerUtil.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest()
class PlayerServiceTest {

    @Autowired
    private PlayerService playerService;

    private final Player JUGADOR_1 = new Player("Messi", "Barcelona", "Liga1", 0, 10, 5, 20, 3, 2, 0, 8.5, 5);
    private final Player JUGADOR_2 = new Player("Mbappe", "PSG", "Liga1", 0, 15, 8, 25, 4, 3, 0, 9.0, 7);

    @Autowired
    private TestService testService;

    @BeforeEach
    void setUp() {
        testService.eliminarJugadores();
    }

    @Test
    void crearJugadorValidoExitosamente() {
        Player nuevo = new Player("Messi", "Barcelona", "Liga1",0, 10, 5, 20, 3, 2,0, 8.5, 5);

        Player guardado = playerService.crear(nuevo);

        assertNotNull(guardado.getId());
        assertEquals("Messi", guardado.getName());
        assertEquals(1, guardado.getCurrentPrice());
        assertEquals(100, guardado.getTokens().size());
    }

    @Test
    void jugadorNuevoTiene100TokensYValeExactamente1() {
        Player nuevo = new Player("Messi", "Barcelona", "Liga1",0, 10, 5, 20, 3, 2,0, 8.5, 5);

        Player guardado = playerService.crear(nuevo);

        assertEquals(1, guardado.getCurrentPrice());
        assertEquals(100, guardado.getTokens().size());
    }

    @Test
    void rechazaCreacionConNombreDuplicado() {
        playerService.crear(JUGADOR_1);

        NombreRepetidoException exception = assertThrows(NombreRepetidoException.class,
            () -> playerService.crear(JUGADOR_1));

        assertTrue(exception.getMessage().contains("Messi"));
    }



    @Test
    void obtieneJugadorPorId() {
        Player creado = playerService.crear(JUGADOR_1);

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
        playerService.crear(JUGADOR_1);
        playerService.crear(JUGADOR_2);

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
    void listaLosTop5JugadoresPorRating() {
        Player jugador1 = getJugadorConRating("Jugador1", 2D);
        playerService.crear(jugador1);

        Player jugador2 = getJugadorConRating("Jugador2", 3D);
        playerService.crear(jugador2);

        Player jugador3 = getJugadorConRating("Jugador3", 4D);
        playerService.crear(jugador3);

        Player jugador4 = getJugadorConRating("Jugador4", 9D);
        playerService.crear(jugador4);

        Player jugador5 = getJugadorConRating("Jugador5", 9.5D);
        playerService.crear(jugador5);

        List<Player> jugadores = playerService.listarTop5JugadoresPorRating();
        List<Player> expectedPlayers = List.of(jugador5, jugador4, jugador3, jugador2, jugador1);


        assertEquals(expectedPlayers.size(), jugadores.size());
        for (int i = 0; i < jugadores.size(); i++) {
            assertEquals(jugadores.get(i).getName(), expectedPlayers.get(i).getName());
        }

    }

    @Test
    void listaLosTop5JugadoresPorRatingConMasDe5Jugadores() {
        Player jugador1 = getJugadorConRating("Jugador1", 2D);
        playerService.crear(jugador1);

        Player jugador2 = getJugadorConRating("Jugador2", 3D);
        playerService.crear(jugador2);

        Player jugador3 = getJugadorConRating("Jugador3", 4D);
        playerService.crear(jugador3);

        Player jugador4 = getJugadorConRating("Jugador4", 9D);
        playerService.crear(jugador4);

        Player jugador5 = getJugadorConRating("Jugador5", 9.5D);
        playerService.crear(jugador5);

        Player jugador6 = getJugadorConRating("Jugador6", 9.9D);
        playerService.crear(jugador6);

        List<Player> jugadores = playerService.listarTop5JugadoresPorRating();
        List<Player> expectedPlayers = List.of(jugador6, jugador5, jugador4, jugador3, jugador2);


        assertEquals(expectedPlayers.size(), jugadores.size());
        for (int i = 0; i < jugadores.size(); i++) {
            assertEquals(jugadores.get(i).getName(), expectedPlayers.get(i).getName());
        }

    }

    @Test
    void listaLosTop5JugadoresPorRatingCuandoNoHayJugadoresDaVacio() {

        List<Player> jugadores = playerService.listarTop5JugadoresPorRating();

        assertTrue(jugadores.isEmpty());

    }

    @Test
    void listarJugadoresPorClub() {
        Player jugador1 = getJugadorConClub("Jugador1", "Club1");
        playerService.crear(jugador1);

        Player jugador2 = getJugadorConClub("Jugador2", "Club1");
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
        Player jugador1 = getJugadorConLiga("Jugador1", "Liga1");
        playerService.crear(jugador1);

        Player jugador2 = getJugadorConLiga("Jugador2", "Liga1");
        playerService.crear(jugador2);

        Player jugador3 = getJugadorConLiga("Jugador3", "Liga2");
        playerService.crear(jugador3);

        Player jugador4 = getJugadorConLiga("Jugador4", "Liga2");
        playerService.crear(jugador4);

        Player jugador5 = getJugadorConLiga("Jugador5", "Liga2");
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
        Player jugador1 = getJugadorConLigaYClub("Jugador1", "Liga1", "Club1");
        playerService.crear(jugador1);

        Player jugador2 = getJugadorConLigaYClub("Jugador2", "Liga1", "Club2");
        playerService.crear(jugador2);

        Player jugador3 = getJugadorConLigaYClub("Jugador3", "Liga2", "Club1");
        playerService.crear(jugador3);

        Player jugador4 = getJugadorConLigaYClub("Jugador4", "Liga2", "Club2");
        playerService.crear(jugador4);

        Player jugador5 = getJugadorConLigaYClub("Jugador5", "Liga2", "Club1");
        playerService.crear(jugador5);

        Page<Player> jugadores = playerService.recuperarTodosConFiltro(new PlayerFilter("Club1", "Liga1"),PageRequest.of(0, 10));
        List<Player> expectedPlayers = List.of(jugador1);


        assertEquals(jugadores.getContent().size(), expectedPlayers.size());
        for (int i = 0; i < jugadores.getContent().size(); i++) {
            assertEquals(jugadores.getContent().get(i).getName(), expectedPlayers.get(i).getName());
        }

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


    @AfterEach
    void tearDown() {
        testService.eliminarJugadores();
    }
}
