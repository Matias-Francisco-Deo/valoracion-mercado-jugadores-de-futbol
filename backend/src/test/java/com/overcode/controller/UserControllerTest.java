package com.overcode.controller;

import com.overcode.controller.dto.AuthDtos;
import com.overcode.controller.dto.user.UserResponseDTO;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    private static final String DEFAULT_USERNAME = "userTestUser";
    private static final String DEFAULT_EMAIL = "usertest@example.com";
    private static final String DEFAULT_PASSWORD = "Password123!";
    private static final String SECOND_USERNAME = "otherUser";
    private static final String SECOND_EMAIL = "other@example.com";
    private static final String SECOND_PASSWORD = "OtherPassword123!";
    private static final Long NON_EXISTENT_ID = 999999L;
    private static final String MALFORMED_ID = "invalid-id";
    private static final String INVALID_BEARER_TOKEN = "Bearer invalid.token.value";

    @LocalServerPort
    private int port;

    @Autowired
    private TestService testService;

    private RestClient restClient;

    @PostConstruct
    public void init() {
        this.restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();
    }

    private AuthDtos.AuthResponse registerUser(String username, String email, String password) {
        var req = new AuthDtos.RegisterRequest(username, email, password);
        ResponseEntity<AuthDtos.AuthResponse> response = restClient.post()
            .uri("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .body(req)
            .retrieve()
            .toEntity(AuthDtos.AuthResponse.class);

        return response.getBody();
    }

    @BeforeEach
    void setUp() {
        testService.eliminarUsuarios();
    }

    @AfterEach
    void tearDown() {
        testService.eliminarUsuarios();
    }

    // ------------------------------ Tests de consulta de usuario por ID ------------------------------

    @Test
    public void obtenerUsuarioPorIdExistenteDevuelveOkConDatosCorrectos() {
        var authUser1 = registerUser(DEFAULT_USERNAME, DEFAULT_EMAIL, DEFAULT_PASSWORD);
        var authUser2 = registerUser(SECOND_USERNAME, SECOND_EMAIL, SECOND_PASSWORD);
        Long user1Id = authUser1.user().id();

        ResponseEntity<UserResponseDTO> response = restClient.get()
            .uri("/users/" + user1Id)
            .header("Authorization", "Bearer " + authUser2.token())
            .retrieve()
            .toEntity(UserResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(user1Id, response.getBody().id());
        assertEquals(DEFAULT_USERNAME, response.getBody().username());
        assertEquals(DEFAULT_EMAIL, response.getBody().email());
    }

    @Test
    public void obtenerUsuarioPorIdNoExponeContrasenaNiCredenciales() {
        var auth = registerUser(DEFAULT_USERNAME, DEFAULT_EMAIL, DEFAULT_PASSWORD);
        Long userId = auth.user().id();

        ResponseEntity<String> response = restClient.get()
            .uri("/users/" + userId)
            .header("Authorization", "Bearer " + auth.token())
            .retrieve()
            .toEntity(String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().contains("password"));
        assertFalse(response.getBody().contains(DEFAULT_PASSWORD));
    }

    @Test
    public void obtenerUsuarioPorIdInexistenteLanzaNotFound() {
        var auth = registerUser(DEFAULT_USERNAME, DEFAULT_EMAIL, DEFAULT_PASSWORD);

        assertThrows(HttpClientErrorException.NotFound.class, () -> restClient.get()
            .uri("/users/" + NON_EXISTENT_ID)
            .header("Authorization", "Bearer " + auth.token())
            .retrieve()
            .toBodilessEntity());
    }

    // TODO SDD TEST FAILURE
    @Test
    @Disabled
    public void obtenerUsuarioConIdInvalidoLanzaBadRequest() {
        var auth = registerUser(DEFAULT_USERNAME, DEFAULT_EMAIL, DEFAULT_PASSWORD);

        assertThrows(HttpClientErrorException.BadRequest.class, () -> restClient.get()
            .uri("/users/" + MALFORMED_ID)
            .header("Authorization", "Bearer " + auth.token())
            .retrieve()
            .toBodilessEntity());
    }

    // ------------------------------ Tests de control de acceso y seguridad ------------------------------

    @Test
    public void obtenerUsuarioPorIdSinTokenLanzaForbidden() {
        assertThrows(HttpClientErrorException.Forbidden.class, () -> restClient.get()
            .uri("/users/" + NON_EXISTENT_ID)
            .retrieve()
            .toBodilessEntity());
    }

    @Test
    public void obtenerUsuarioPorIdConTokenInvalidoLanzaForbidden() {
        assertThrows(HttpClientErrorException.Forbidden.class, () -> restClient.get()
            .uri("/users/" + NON_EXISTENT_ID)
            .header("Authorization", INVALID_BEARER_TOKEN)
            .retrieve()
            .toBodilessEntity());
    }
}
