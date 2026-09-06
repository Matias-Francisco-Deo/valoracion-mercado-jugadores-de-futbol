package com.overcode.integration;

import com.overcode.controller.dto.AuthDtos;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthRegisterIT extends SecurityTestBase {
n    @LocalServerPort
    private int port;
n    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void registerSucceeds() {
        var req = new AuthDtos.RegisterRequest("integuser", "integ@example.com", "Password123!");
        ResponseEntity<AuthDtos.AuthResponse> resp = restTemplate.postForEntity("/auth/register", req, AuthDtos.AuthResponse.class);
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertNotNull(resp.getBody().token());
        assertFalse(resp.getBody().token().isEmpty());
    }
}
