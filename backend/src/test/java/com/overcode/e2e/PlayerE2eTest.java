package com.overcode.e2e;

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

import static com.overcode.testUtils.TestPlayerUtil.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PlayerE2eTest {

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

        assert response.getBody() != null;
        return response.getBody().token();
    }

    @BeforeEach
    void setUp() {
        testService.eliminarJugadoresYEquipos();
        testService.eliminarUsuarios();
    }

    @AfterEach
    void tearDown() {
        testService.eliminarJugadoresYEquipos();
        testService.eliminarUsuarios();
    }

    // ------------------------------ Tests de listado de jugadores ------------------------------

    @Test
    void listarJugadoresConBaseVaciaDevuelveListaVacia() {
        String token = obtainAuthToken();

        ResponseEntity<List<PlayerResponseDTO>> response = restClient.get()
            .uri("/players")
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<>() {
            });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void listarJugadoresConJugadoresExistentesDevuelveListaCompleta() {
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
    void listarJugadoresConJugadoresExistentesConFiltroPorClubDevuelveDeEseClub() {
        String token = obtainAuthToken();
        playerService.crear(getJugadorConClub(PLAYER_NAME, "Club1"));
        playerService.crear(getJugadorConClub(SECOND_PLAYER_NAME, "Club2"));

        ResponseEntity<List<PlayerResponseDTO>> response = restClient.get()
                .uri("/players?clubName=Club1")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<PlayerResponseDTO> players = response.getBody();
        assertNotNull(players);
        assertEquals(1, players.size());
        assertEquals(PLAYER_NAME, players.getFirst().name());
    }

    @Test
    void listarJugadoresConJugadoresExistentesConFiltroPorClubNameCamelCaseDevuelveDeEseClub() {
        String token = obtainAuthToken();
        playerService.crear(getJugadorConClub(PLAYER_NAME, "ClubUno"));
        playerService.crear(getJugadorConClub(SECOND_PLAYER_NAME, "ClubDos"));

        ResponseEntity<List<PlayerResponseDTO>> response = restClient.get()
                .uri("/players?clubName=ClubUno")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<PlayerResponseDTO> players = response.getBody();
        assertNotNull(players);
        assertEquals(1, players.size());
        assertEquals(PLAYER_NAME, players.getFirst().name());
    }

    @Test
    void listarJugadoresConJugadoresExistentesConFiltroPorLigaDevuelveDeEsaLiga() {
        String token = obtainAuthToken();
        playerService.crear(getJugadorConLiga(PLAYER_NAME, "Liga1"));
        playerService.crear(getJugadorConLiga(SECOND_PLAYER_NAME, "Liga2"));

        ResponseEntity<List<PlayerResponseDTO>> response = restClient.get()
                .uri("/players?league=Liga1")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<PlayerResponseDTO> players = response.getBody();
        assertNotNull(players);
        assertEquals(1, players.size());
        assertEquals(PLAYER_NAME, players.getFirst().name());
    }

    @Test
    void ligarJugadoresPorVariosFiltros() {
        String token = obtainAuthToken();
        playerService.crear(getJugadorConLigaYClub(PLAYER_NAME, "Liga1", "Club1"));
        playerService.crear(getJugadorConLigaYClub(SECOND_PLAYER_NAME, "Liga2", "Club2"));
        playerService.crear(getJugadorConLigaYClub("Jugador3", "Liga2", "Club1"));

        ResponseEntity<List<PlayerResponseDTO>> response = restClient.get()
                .uri("/players?clubName=Club1&league=Liga1")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<PlayerResponseDTO> players = response.getBody();
        assertNotNull(players);
        assertEquals(1, players.size());
        assertEquals(PLAYER_NAME, players.getFirst().name());
    }

    @Test
    void listarTopJugadoresTraeOrdenadosPorRating() {
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
    void listarTopJugadoresTrae5AunqueHayaMas() {
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
    void listarTopJugadoresSinJugadoresDaVacio() {
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
    void obtenerJugadorPorIdExistenteDevuelveOkConDatosCorrectos() {
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
    void obtenerJugadorPorIdInexistenteLanzaNotFound() {
        String token = obtainAuthToken();

        assertThrows(HttpClientErrorException.NotFound.class, () -> restClient.get()
            .uri("/players/" + NON_EXISTENT_ID)
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .toBodilessEntity());
    }

    @Test
    void syncMetricsSinTokenLanzaForbidden() {
        assertThrows(HttpClientErrorException.Forbidden.class, () -> restClient.post()
            .uri("/players/sync-metrics")
            .retrieve()
            .toBodilessEntity());
    }



}
