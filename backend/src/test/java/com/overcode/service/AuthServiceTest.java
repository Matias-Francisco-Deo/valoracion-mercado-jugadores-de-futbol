package com.overcode.service;

import com.overcode.controller.dto.auth.AuthResponse;
import com.overcode.model.User;
import com.overcode.service.exception.AuthenticationException;
import com.overcode.service.exception.ConflictException;
import com.overcode.service.impl.AuthServiceImpl;
import com.overcode.testUtils.TestService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
@SpringBootTest()
public class AuthServiceTest {

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private TestService testService;
    private User userRegistrer;
    private User userLogin;

    @BeforeEach
    void setUp() {
        userRegistrer = new User("jorge", "test@example.com", "Password");
        userLogin = new User("temporal", "test@example.com", "Password");
        testService.eliminarUsuarios();
    }

    //------------------------------Tests de registro------------------------------
    @Test
    void RegistroConCredencialesValidas() {
        AuthResponse response = authService.register(userRegistrer);

        assertNotNull(response);
        assertNotNull( response.token());
        assertEquals("jorge", response.user().username());
        assertEquals("test@example.com", response.user().email());
    }

    @Test
    void RegistroConEmailExistente() {
        AuthResponse response = authService.register(userRegistrer);

        userRegistrer = new User("ana", "test@example.com", "Password123");

        assertThrows(ConflictException.class, () -> authService.register(userRegistrer));
    }


    //------------------------------Tests de login------------------------------
    @Test
    void loginConCredencialesValidas() {
        authService.register(userRegistrer);

        AuthResponse response = authService.login(userLogin);

        assertNotNull(response);
        assertNotNull( response.token());
        assertEquals("jorge", response.user().username());
        assertEquals("test@example.com", response.user().email());
    }

    @Test
    void loginConEmailNoRegistrado() {
        User badRequest = new User("nonexistent", "nonexistent@example.com", "Password");
        assertThrows(AuthenticationException.class, () -> authService.login(badRequest));
    }

    @Test
    void loginConPasswordIncorrecta() {
        authService.register(userRegistrer);

        User badRequest = new User("jorge", "test@example.com", "WrongPassword");
        assertThrows(AuthenticationException.class, () -> authService.login(badRequest));
    }

    @AfterEach
    void teardown() {
        testService.eliminarUsuarios();
    }
}