package com.overcode.service;

import com.overcode.model.Player;
import com.overcode.persistence.repository.dao.PlayerDAOJPA;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.exception.ValidationException;
import com.overcode.service.interfaces.PlayerService;
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

    @Autowired
    private PlayerDAOJPA playerDAOJPA;

    @BeforeEach
    void setUp() {
        playerDAOJPA.deleteAll();
    }

    @AfterEach
    void tearDown() {
        playerDAOJPA.deleteAll();
    }

    @Test
    void crearJugadorValidoExitosamente() {
        Player nuevo = new Player(null, "Messi", 120, 50);

        Player guardado = playerService.crear(nuevo);

        assertNotNull(guardado.getId());
        assertEquals("Messi", guardado.getName());
        assertEquals(120, guardado.getCurrentPrice());
        assertEquals(50, guardado.getTotalTokensIssued());
    }

    @Test
    void rechazaCreacionConNombreDuplicado() {
        playerService.crear(new Player(null, "Mbappe", 100, 10));

        ValidationException exception = assertThrows(ValidationException.class,
            () -> playerService.crear(new Player(null, "Mbappe", 150, 20)));

        assertTrue(exception.getMessage().contains("Mbappe"));
    }

    @Test
    void obtieneJugadorPorId() {
        Player creado = playerService.crear(new Player(null, "Ronaldo", 90, 30));

        Player encontrado = playerService.recuperar(creado.getId());

        assertEquals(creado.getId(), encontrado.getId());
        assertEquals("Ronaldo", encontrado.getName());
    }

    @Test
    void lanzaNotFoundCuandoNoExisteJugador() {
        assertThrows(EntidadNoEncontradaException.class, () -> playerService.recuperar(999L));
    }

    @Test
    void listaTodosLosJugadores() {
        playerService.crear(new Player(null, "Player One", 100, 10));
        playerService.crear(new Player(null, "Player Two", 200, 20));

        List<Player> jugadores = playerService.recuperarTodos();

        assertEquals(2, jugadores.size());
        assertTrue(jugadores.stream().anyMatch(player -> player.getName().equals("Player One")));
        assertTrue(jugadores.stream().anyMatch(player -> player.getName().equals("Player Two")));
    }

    @Test
    void listaVaciaCuandoNoHayJugadores() {
        assertTrue(playerService.recuperarTodos().isEmpty());
    }
}
