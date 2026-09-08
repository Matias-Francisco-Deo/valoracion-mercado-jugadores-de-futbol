package com.overcode.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpClientErrorException;
import jakarta.annotation.PostConstruct;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JwtFailureIT {

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @PostConstruct
    public void init() {
        this.restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();
    }

    @Test
    public void accessProtectedEndpoint_WithInvalidToken_ReturnsForbidden() {
        try {
            restClient.get()
                .uri("/users/1")
                .header("Authorization", "Bearer invalid.token.here")
                .retrieve()
                .toBodilessEntity();
        } catch (HttpClientErrorException.Forbidden e) {
            assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());
        }
    }
}
