package com.overcode.service;

import com.overcode.controller.dto.auth.LoginRequest;
import com.overcode.model.User;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.security.JwtUtil;
import com.overcode.security.PasswordHasher;
import com.overcode.service.exception.AuthenticationException;
import com.overcode.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
/*
@ExtendWith(MockitoExtension.class)
class AuthServiceLoginTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private LoginRequest validRequest;
    private User mockUser;

    @BeforeEach
    void setUp() {
        validRequest = new LoginRequest("test@example.com", "password123");
        mockUser = new User("testuser", "test@example.com", "hashedPassword");
    }

    @Test
    void login_Success() {
        when(userRepository.findByEmail(validRequest.email())).thenReturn(Optional.of(mockUser));
        when(passwordHasher.matches(validRequest.password(), mockUser.getPassword())).thenReturn(true);
        
        when(jwtUtil.generateToken(anyString(), anyMap())).thenReturn("mockJwtToken");
        when(jwtUtil.getExpiration(anyString())).thenReturn(new Date(System.currentTimeMillis() + 86400000));

        AuthResponse response = authService.login(validRequest.email(), validRequest.password());

        assertNotNull(response);
        assertEquals("mockJwtToken", response.token());
        assertEquals("testuser", response.user().username());
        assertEquals("test@example.com", response.user().email());
    }

    @Test
    void login_ThrowsAuthenticationException_WhenEmailNotFound() {
        when(userRepository.findByEmail(validRequest.email())).thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class, () -> authService.login(validRequest.email(), validRequest.password()));

        verify(passwordHasher, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyMap());
    }

    @Test
    void login_ThrowsAuthenticationException_WhenPasswordMismatch() {
        when(userRepository.findByEmail(validRequest.email())).thenReturn(Optional.of(mockUser));
        when(passwordHasher.matches(validRequest.password(), mockUser.getPassword())).thenReturn(false);

        assertThrows(AuthenticationException.class, () -> authService.login(validRequest.email(), validRequest.password()));

        verify(jwtUtil, never()).generateToken(anyString(), anyMap());
    }
}
*/