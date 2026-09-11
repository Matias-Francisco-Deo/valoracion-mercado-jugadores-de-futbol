package com.overcode.controller;

import com.overcode.controller.dto.auth.AuthResponse;
import com.overcode.controller.dto.auth.LoginRequest;
import com.overcode.controller.dto.auth.RegisterRequest;
import com.overcode.testUtils.TestService;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
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
public class AuthControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestService testService;

    private RestClient restClient;

    @PostConstruct
    public void init() {
        this.restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();
    }
    //------------------------------Tests de registro------------------------------
    @Test
    public void registrarUsuarioConDatosValidos() {
        var req = new RegisterRequest("User", "register@example.com", "Password123!");

        ResponseEntity<AuthResponse> response = restClient.post()
            .uri("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .body(req)
            .retrieve()
            .toEntity(AuthResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().token());
        assertFalse(response.getBody().token().isBlank());
    }

    @Test
    public void alCrearUsuarioNuevoTieneDatosDeInicializacionCreditos0YSinTokens() {
        var req = new RegisterRequest("User", "register@example.com", "Password123!");

        ResponseEntity<AuthResponse> response = restClient.post()
                .uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(req)
                .retrieve()
                .toEntity(AuthResponse.class);

        assert response.getBody() != null;
        assertEquals(0, response.getBody().user().creditBalance());
        assertEquals(0, response.getBody().user().tokens().size());
    }

    @Test
    public void registrarUsuarioConEmailInvalido() {
        var req = new RegisterRequest("invalidEmail", "invalid-email", "Password123!");

        assertThrows(HttpClientErrorException.BadRequest.class, () -> restClient.post()
            .uri("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .body(req)
            .retrieve()
            .toBodilessEntity());
    }

    @Test
    public void registrarUsuarioSinEmail() {
        var req = new RegisterRequest("sinEmail", "", "Password123!");

        assertThrows(HttpClientErrorException.BadRequest.class, () -> restClient.post()
                .uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(req)
                .retrieve()
                .toBodilessEntity());
    }

    @Test
    public void registrarUsuarioSinContraseña() {
        var req = new RegisterRequest("sinContraseña", "login@example.com", "");

        assertThrows(HttpClientErrorException.BadRequest.class, () -> restClient.post()
                .uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(req)
                .retrieve()
                .toBodilessEntity());
    }

    @Test
    public void registrarUsuarioSinNombre() {
        var req = new RegisterRequest("", "login@example.com", "Password123");

        assertThrows(HttpClientErrorException.BadRequest.class, () -> restClient.post()
                .uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(req)
                .retrieve()
                .toBodilessEntity());
    }
    //------------------------------Tests de login------------------------------
    @Test
    public void loginConDatosValidos() {
        registerUser("loginUser", "login@example.com", "Password123!");

        var req = new LoginRequest("login@example.com", "Password123!");
        ResponseEntity<AuthResponse> response = restClient.post()
            .uri("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(req)
            .retrieve()
            .toEntity(AuthResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().token());
        assertFalse(response.getBody().token().isBlank());
    }

    @Test
    public void loginConContraseñaIncorrecta() {
        registerUser("loginUser", "login@example.com", "Password123!");

        var req = new LoginRequest("login@example.com", "WrongPassword123!");

        assertThrows(HttpClientErrorException.Unauthorized.class, () -> restClient.post()
            .uri("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(req)
            .retrieve()
            .toBodilessEntity());
    }

    @Test
    public void loginConEmailIncorrecta() {
        registerUser("loginUser", "login@example.com", "Password123!");

        var req = new LoginRequest("wrong@example.com", "Password123!");

        assertThrows(HttpClientErrorException.Unauthorized.class, () -> restClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(req)
                .retrieve()
                .toBodilessEntity());
    }
    //------------------------------Tests de accesos a endpoints protegidos------------------------------
    @Test
    public void accederEndpointProtegidoSinToken() {
        assertThrows(HttpClientErrorException.Forbidden.class, () -> restClient.get()
            .uri("/users/1")
            .retrieve()
            .toBodilessEntity());
    }

    @Test
    public void accederEndpointProtegidoConTokenValido() {
        var auth = registerUser("protectedUser", "protected@example.com", "Password123!");
        //como el token es válido, debería poder acceder al endpoint protegido y obtener Not Found porque el usuario no existe en lugar de Forbidden
        assertThrows(HttpClientErrorException.NotFound.class, () -> restClient.get()
            .uri("/users/1")
            .header("Authorization", "Bearer " + auth.token())
            .retrieve()
            .toBodilessEntity());
    }

    @Test
    public void accederEndpointProtegidoConTokenInvalido() {
        assertThrows(HttpClientErrorException.Forbidden.class, () -> restClient.get()
            .uri("/users/1")
            .header("Authorization", "Bearer invalid.token.value")
            .retrieve()
            .toBodilessEntity());
    }

    //Metodo para registrar un usuario para tests
    private AuthResponse registerUser(String username, String email, String password) {
        var req = new RegisterRequest(username, email, password);
        ResponseEntity<AuthResponse> response = restClient.post()
            .uri("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .body(req)
            .retrieve()
            .toEntity(AuthResponse.class);

        return response.getBody();
    }

    @AfterEach
    void teardown() {
        testService.eliminarUsuarios();
    }
}