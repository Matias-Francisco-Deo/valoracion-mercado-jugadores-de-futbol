package com.overcode.service;

import com.overcode.model.Player;
import com.overcode.persistence.repository.dao.PlayerDAOJPA;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.exception.NombreJugadorRepetidoException;
import com.overcode.service.exception.ValidationException;
import com.overcode.service.interfaces.PlayerService;
import com.overcode.testUtils.TestService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class PlayerServiceTest {

    @Autowired
    private PlayerService playerService;

    private final Player JUGADOR_1 = new Player("Messi");
    private final Player JUGADOR_2 = new Player("Mbappe");

    @Autowired
    private TestService testService;

    @BeforeEach
    void setUp() {
        testService.eliminarJugadores();
    }

    @AfterEach
    void tearDown() {
        testService.eliminarJugadores();
    }

    @Test
    void crearJugadorValidoExitosamente() {
        Player nuevo = new Player("Messi");

        Player guardado = playerService.crear(nuevo);

        assertNotNull(guardado.getId());
        assertEquals("Messi", guardado.getName());
        assertEquals(1, guardado.getCurrentPrice());
        assertEquals(100, guardado.getTotalTokensIssued());
    }

    @Test
    void jugadorNuevoTiene100TokensYValeExactamente1() {
        Player nuevo = new Player("Messi");

        Player guardado = playerService.crear(nuevo);

        assertEquals(1, guardado.getCurrentPrice());
        assertEquals(100, guardado.getTotalTokensIssued());
    }

    @Test
    void rechazaCreacionConNombreDuplicado() {
        playerService.crear(JUGADOR_1);

        NombreJugadorRepetidoException exception = assertThrows(NombreJugadorRepetidoException.class,
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
}
