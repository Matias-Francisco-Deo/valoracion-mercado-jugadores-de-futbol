package com.overcode.controller;

import com.overcode.controller.dto.AuthDtos;
import com.overcode.controller.dto.player.PlayerResponseDTO;
import com.overcode.model.Player;
import com.overcode.service.interfaces.PlayerService;
import com.overcode.testUtils.TestService;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
    private static final String MALFORMED_ID = "invalid-id";
    private static final String INVALID_BEARER_TOKEN = "Bearer invalid.token.value";

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
        var req = new AuthDtos.RegisterRequest(DEFAULT_USERNAME, DEFAULT_EMAIL, DEFAULT_PASSWORD);
        ResponseEntity<AuthDtos.AuthResponse> response = restClient.post()
            .uri("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .body(req)
            .retrieve()
            .toEntity(AuthDtos.AuthResponse.class);

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
    public void listarJugadoresConBaseVaciaDevuelveListaVacia() { // TODO este test luego del scraping es posible que no pase
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
    public void listarJugadoresConJugadoresExistentesDevuelveListaCompleta() { // TODO este test luego del scraping es posible que no pase
        String token = obtainAuthToken();
        playerService.crear(new Player(PLAYER_NAME));
        playerService.crear(new Player(SECOND_PLAYER_NAME));

        ResponseEntity<List<PlayerResponseDTO>> response = restClient.get()
            .uri("/players")
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<List<PlayerResponseDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().stream().anyMatch(p -> PLAYER_NAME.equals(p.name())));
        assertTrue(response.getBody().stream().anyMatch(p -> SECOND_PLAYER_NAME.equals(p.name())));
    }

    // ------------------------------ Tests de consulta de jugador por ID ------------------------------

    @Test
    public void obtenerJugadorPorIdExistenteDevuelveOkConDatosCorrectos() {
        String token = obtainAuthToken();
        Player guardado = playerService.crear(new Player(PLAYER_NAME));

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

    // TODO SDD TEST FAILURE
    @Test
    @Disabled
    public void obtenerJugadorConIdInvalidoLanzaBadRequest() {
        String token = obtainAuthToken();

        assertThrows(HttpClientErrorException.BadRequest.class, () -> restClient.get()
            .uri("/players/" + MALFORMED_ID)
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .toBodilessEntity());
    }

    // ------------------------------ Tests de control de acceso y seguridad ------------------------------

    @Test
    public void listarJugadoresSinTokenLanzaForbidden() {
        assertThrows(HttpClientErrorException.Forbidden.class, () -> restClient.get()
            .uri("/players")
            .retrieve()
            .toBodilessEntity());
    }

    @Test
    public void listarJugadoresConTokenInvalidoLanzaForbidden() {
        assertThrows(HttpClientErrorException.Forbidden.class, () -> restClient.get()
            .uri("/players")
            .header("Authorization", INVALID_BEARER_TOKEN)
            .retrieve()
            .toBodilessEntity());
    }

    @Test
    public void obtenerJugadorPorIdSinTokenLanzaForbidden() {
        assertThrows(HttpClientErrorException.Forbidden.class, () -> restClient.get()
            .uri("/players/" + NON_EXISTENT_ID)
            .retrieve()
            .toBodilessEntity());
    }

    @Test
    public void obtenerJugadorPorIdConTokenInvalidoLanzaForbidden() {
        assertThrows(HttpClientErrorException.Forbidden.class, () -> restClient.get()
            .uri("/players/" + NON_EXISTENT_ID)
            .header("Authorization", INVALID_BEARER_TOKEN)
            .retrieve()
            .toBodilessEntity());
    }
}
