package com.overcode.integration;

import com.overcode.controller.dto.AuthDtos;
import com.overcode.testUtils.TestService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
//TODO borrar?
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;
import jakarta.annotation.PostConstruct;

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

    @Test
    public void registrarUserCorrectamente() {
        var req = new AuthDtos.RegisterRequest("succest", "integ@example.com", "Password123!");
        ResponseEntity<AuthDtos.AuthResponse> resp = restClient.post()
                .uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(req)
                .retrieve()
                .toEntity(AuthDtos.AuthResponse.class);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertNotNull(resp.getBody().token());
        assertFalse(resp.getBody().token().isEmpty());
    }
/*
    @Test
    public void registroDeUsuarioConEmailInvalido() {
        var req = new AuthDtos.RegisterRequest("failureEmail", "integexample.com", "Password123!");
        ResponseEntity<AuthDtos.AuthResponse> resp = restClient.post()
                .uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(req)
                .retrieve()
                .toEntity(AuthDtos.AuthResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }*/

    @Test
    public void loginSucceeds() {
        var reg = new AuthDtos.RegisterRequest("loginuser", "login@example.com", "Password123!");
        ResponseEntity<AuthDtos.AuthResponse> r1 = restClient.post()
            .uri("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .body(reg)
            .retrieve()
            .toEntity(AuthDtos.AuthResponse.class);

        var login = new AuthDtos.LoginRequest("login@example.com", "Password123!");
        ResponseEntity<AuthDtos.AuthResponse> r2 = restClient.post()
            .uri("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(login)
            .retrieve()
            .toEntity(AuthDtos.AuthResponse.class);
            
        assertEquals(HttpStatus.OK, r2.getStatusCode());
        assertNotNull(r2.getBody());
        assertNotNull(r2.getBody().token());
    }

    @AfterEach
    void teardown() {
        testService.eliminarUsuarios();
    }
}
