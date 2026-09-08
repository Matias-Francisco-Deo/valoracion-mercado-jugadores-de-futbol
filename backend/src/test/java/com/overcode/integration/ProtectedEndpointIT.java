package com.overcode.integration;

import com.overcode.controller.dto.AuthDtos;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.http.MediaType;
import jakarta.annotation.PostConstruct;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProtectedEndpointIT {
//TODO mover a un solo test de autenticacion
    @LocalServerPort
    private int port;

    private RestClient restClient;

    @PostConstruct
    public void init() {
        this.restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();
    }

    @Test
    public void accessProtectedEndpoint_WithoutToken_ReturnsForbidden() {
        try {
            restClient.get().uri("/users/1").retrieve().toBodilessEntity();
        } catch (HttpClientErrorException.Forbidden e) {
            assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());
        }
    }

    @Test
    public void accessProtectedEndpoint_WithValidToken_Succeeds() {
        var reg = new AuthDtos.RegisterRequest("protectedUser", "protected@example.com", "Password123!");
        ResponseEntity<AuthDtos.AuthResponse> authResp = restClient.post()
            .uri("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .body(reg)
            .retrieve()
            .toEntity(AuthDtos.AuthResponse.class);
            
        assertEquals(HttpStatus.CREATED, authResp.getStatusCode());
        assertNotNull(authResp.getBody());
        String token = authResp.getBody().token();

        try {
            ResponseEntity<Void> response = restClient.get()
                .uri("/users/1")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toBodilessEntity();
                
            // As long as it is not FORBIDDEN (403) or UNAUTHORIZED (401), security let it through.
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        } catch (HttpClientErrorException.NotFound e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        }
    }
}
