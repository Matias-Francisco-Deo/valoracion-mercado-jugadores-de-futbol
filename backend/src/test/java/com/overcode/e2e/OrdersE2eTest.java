package com.overcode.e2e;

import com.overcode.controller.dto.auth.AuthResponse;
import com.overcode.controller.dto.auth.RegisterRequest;
import com.overcode.controller.dto.token.TokenPageResponseDTO;
import com.overcode.controller.dto.token.TokenSearchResponseDTO;
import com.overcode.service.interfaces.OrderService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.util.List;

import static com.overcode.testUtils.TestPlayerUtil.getJugadorConNombre;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrdersE2eTest {
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
    private OrderService orderService;
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
        playerService.crear(getJugadorConNombre(PLAYER_NAME));
        playerService.crear(getJugadorConNombre(SECOND_PLAYER_NAME));
    }
    @AfterEach
    void tearDown() {
        testService.eliminarUsuarios();
    }
//TODO arreglar test con disable y quitar token jwt si no son necesarios
    @Test
    void listarTokensEnVentaConBaseVaciaDevuelveListaVacia(){
        String token = obtainAuthToken();

        ResponseEntity<TokenPageResponseDTO> response = restClient.get()
                .uri("/orders/for-sale")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(TokenPageResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().content().isEmpty());

        assertEquals(0, response.getBody().totalElements());
        assertEquals(0, response.getBody().totalPages());
    }

    @Test
    @Disabled("al crear los jugadores se deben crear los tokens tambien")
    void listarTokensEnVentaConTokensExistentesDevuelveListaCompleta() {
        String token = obtainAuthToken();

        ResponseEntity<TokenPageResponseDTO> response = restClient.get()
                .uri("/orders/for-sale")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(TokenPageResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        List<TokenSearchResponseDTO> tokens = response.getBody().content();
        assertEquals(2, tokens.size());
        assertTrue(tokens.stream().anyMatch(t -> PLAYER_NAME.equals(t.playerName())));
        assertTrue(tokens.stream().anyMatch(t -> SECOND_PLAYER_NAME.equals(t.playerName())));

        assertEquals(10, tokens.get(0).quantity());
        assertEquals(100L, tokens.get(0).currentPrice());
    }

    @Test
    @Disabled("al crear los jugadores se deben crear los tokens tambien")
    void buscarTokensEnVentaPorNombreDevuelveSoloLosCoincidentes() {
        String token = obtainAuthToken();

        ResponseEntity<TokenPageResponseDTO> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/orders/for-sale")
                        .queryParam("playerName", PLAYER_NAME)
                        .build())
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(TokenPageResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        List<TokenSearchResponseDTO> tokens = response.getBody().content();

    }
}
