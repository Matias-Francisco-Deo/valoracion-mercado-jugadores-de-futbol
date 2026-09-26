package com.overcode.controller;

import com.overcode.controller.dto.auth.AuthResponse;
import com.overcode.controller.dto.auth.RegisterRequest;
import com.overcode.controller.dto.player.PlayerResponseDTO;
import com.overcode.model.Player;
import com.overcode.service.interfaces.PlayerService;
import com.overcode.testUtils.TestService;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PlayerControllerTest {

    private static final String DEFAULT_USERNAME = "playerTestUser";
    private static final String DEFAULT_EMAIL = "playertest@example.com";
    private static final String DEFAULT_PASSWORD = "Password123!";
    private static final String PLAYER_NAME = "Player1";
    private static final String SECOND_PLAYER_NAME = "Player2";
    private static final Long NON_EXISTENT_ID = -1L;

    @LocalServerPort
    private int port;

    @Autowired
    private TestService testService;

    @Autowired
    private PlayerService playerService;

    private RestClient restClient;

    @PostConstruct
    public void init() {
        this.restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();
    }

    private String obtainAuthToken() {
        var req = new RegisterRequest(DEFAULT_USERNAME, DEFAULT_EMAIL, DEFAULT_PASSWORD);
        ResponseEntity<AuthResponse> response = restClient.post()
            .uri("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .body(req)
            .retrieve()
            .toEntity(AuthResponse.class);

        return response.getBody().token();
    }

    @BeforeEach
    void setUp() {
        testService.eliminarJugadores();
        testService.eliminarUsuarios();
    }

    @AfterEach
    void tearDown() {
        testService.eliminarJugadores();
        testService.eliminarUsuarios();
    }

    // ------------------------------ Tests de listado de jugadores ------------------------------

    @Test
    public void listarJugadoresConBaseVaciaDevuelveListaVacia() {
        String token = obtainAuthToken();

        ResponseEntity<List<PlayerResponseDTO>> response = restClient.get()
            .uri("/players")
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<List<PlayerResponseDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    public void listarJugadoresConJugadoresExistentesDevuelveListaCompleta() {
        String token = obtainAuthToken();
        playerService.crear(getJugadorConNombre(PLAYER_NAME));
        playerService.crear(getJugadorConNombre(SECOND_PLAYER_NAME));

        ResponseEntity<List<PlayerResponseDTO>> response = restClient.get()
            .uri("/players")
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<>() {
            });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().stream().anyMatch(p -> PLAYER_NAME.equals(p.name())));
        assertTrue(response.getBody().stream().anyMatch(p -> SECOND_PLAYER_NAME.equals(p.name())));
    }

    @Test
    public void listarTopJugadoresTraeOrdenadosPorRating() {
        String token = obtainAuthToken();
        Player player1 = playerService.crear(getJugadorConRating(PLAYER_NAME, 9.5D));
        Player player2 = playerService.crear(getJugadorConRating(SECOND_PLAYER_NAME, 8.5D));

        ResponseEntity<List<PlayerResponseDTO>> response = restClient.get()
                .uri("/players/top")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<PlayerResponseDTO> players = response.getBody();

        assertNotNull(players);
        assertEquals(2, players.size());
        assertEquals(players.get(0).id(), player1.getId());
        assertEquals(players.get(1).id(), player2.getId());

    }

    @Test
    public void listarTopJugadoresTrae5AunqueHayaMas() {
        String token = obtainAuthToken();
        playerService.crear(getJugadorConRating("jugador1", 9.5D));
        playerService.crear(getJugadorConRating("jugador2", 8.5D));
        playerService.crear(getJugadorConRating("jugador3", 7.5D));
        playerService.crear(getJugadorConRating("jugador4", 6.5D));
        playerService.crear(getJugadorConRating("jugador5", 5.5D));
        playerService.crear(getJugadorConRating("jugador6", 5.5D));

        ResponseEntity<List<PlayerResponseDTO>> response = restClient.get()
                .uri("/players/top")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<PlayerResponseDTO> players = response.getBody();

        assertNotNull(players);
        assertEquals(5, players.size());

    }

    @Test
    public void listarTopJugadoresSinJugadoresDaVacio() {
        String token = obtainAuthToken();

        ResponseEntity<List<PlayerResponseDTO>> response = restClient.get()
                .uri("/players/top")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<PlayerResponseDTO> players = response.getBody();

        assertNotNull(players);
        assertTrue(players.isEmpty());

    }

    // ------------------------------ Tests de consulta de jugador por ID ------------------------------

    @Test
    public void obtenerJugadorPorIdExistenteDevuelveOkConDatosCorrectos() {
        String token = obtainAuthToken();
        Player guardado = playerService.crear(getJugadorConNombre(PLAYER_NAME));

        ResponseEntity<PlayerResponseDTO> response = restClient.get()
            .uri("/players/" + guardado.getId())
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .toEntity(PlayerResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(guardado.getId(), response.getBody().id());
        assertEquals(PLAYER_NAME, response.getBody().name());
        assertEquals(1, response.getBody().currentPrice()); // como es recién creado, esto es válido
        assertEquals(100, response.getBody().tokens().size());
    }

    @Test
    public void obtenerJugadorPorIdInexistenteLanzaNotFound() {
        String token = obtainAuthToken();

        assertThrows(HttpClientErrorException.NotFound.class, () -> restClient.get()
            .uri("/players/" + NON_EXISTENT_ID)
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .toBodilessEntity());
    }

    @Test
    public void syncMetricsSinTokenLanzaForbidden() {
        assertThrows(HttpClientErrorException.Forbidden.class, () -> restClient.post()
            .uri("/players/sync-metrics")
            .retrieve()
            .toBodilessEntity());
    }

    private Player getJugadorConNombre(String name) {
        return new Player(name, "Club", 10, 5, 20, 3, 2, 2.0, 5);
    }

    private Player getJugadorConRating(String name, Double rating) {
        return new Player(name, "Club", 10, 5, 20, 3, 2, rating, 5);
    }
}
