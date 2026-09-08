package com.overcode.integration;

import com.overcode.controller.dto.AuthDtos;
import org.junit.jupiter.api.Test;
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
public class AuthRegisterIT {

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @PostConstruct
    public void init() {
        this.restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();
    }

    @Test
    public void registerSucceeds() {
        var req = new AuthDtos.RegisterRequest("integuser", "integ@example.com", "Password123!");
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
}
