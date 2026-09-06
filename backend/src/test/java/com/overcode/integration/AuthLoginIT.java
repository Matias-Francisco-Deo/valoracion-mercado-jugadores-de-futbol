package com.overcode.integration;

import com.overcode.controller.dto.AuthDtos;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
//TODO borrar?
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthLoginIT extends SecurityTestBase {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void loginSucceedsAfterRegister() {
        var reg = new AuthDtos.RegisterRequest("loginuser", "login@example.com", "Password123!");
        ResponseEntity<AuthDtos.AuthResponse> r1 = restTemplate.postForEntity("/auth/register", reg, AuthDtos.AuthResponse.class);
        assertEquals(HttpStatus.CREATED, r1.getStatusCode());

        var login = new AuthDtos.LoginRequest("login@example.com", "Password123!");
        ResponseEntity<AuthDtos.AuthResponse> r2 = restTemplate.postForEntity("/auth/login", login, AuthDtos.AuthResponse.class);
        assertEquals(HttpStatus.OK, r2.getStatusCode());
        assertNotNull(r2.getBody());
        assertNotNull(r2.getBody().token());
    }
}
