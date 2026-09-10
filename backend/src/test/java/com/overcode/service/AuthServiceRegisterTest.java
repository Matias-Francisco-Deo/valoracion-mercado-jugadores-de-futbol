package com.overcode.service;

import com.overcode.controller.dto.AuthDtos.AuthResponse;
import com.overcode.controller.dto.AuthDtos.RegisterRequest;
import com.overcode.model.User;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.security.JwtUtil;
import com.overcode.security.PasswordHasher;
import com.overcode.service.exception.ConflictException;
import com.overcode.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceRegisterTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new RegisterRequest("testuser", "test@example.com", "password123");
    }

    @Test
    void register_Success() {
        when(userRepository.existsByEmail(validRequest.email())).thenReturn(false);
        when(passwordHasher.hash(validRequest.password())).thenReturn("hashedPassword");
        
        User savedUser = new User(1L, "testuser", "test@example.com", "hashedPassword", 0, 0);
        when(userRepository.guardar(any(User.class))).thenReturn(savedUser);
        
        when(jwtUtil.generateToken(anyString(), anyMap())).thenReturn("mockJwtToken");
        when(jwtUtil.getExpiration(anyString())).thenReturn(new Date(System.currentTimeMillis() + 86400000));

        AuthResponse response = authService.register(validRequest);

        assertNotNull(response);
        assertEquals("mockJwtToken", response.token());
        assertEquals("testuser", response.user().username());
        assertEquals("test@example.com", response.user().email());
        
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).guardar(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        
        assertEquals("test@example.com", capturedUser.getEmail());
        assertEquals("hashedPassword", capturedUser.getPassword());
    }

    @Test
    void register_ThrowsConflictException_WhenEmailExists() {
        when(userRepository.existsByEmail(validRequest.email())).thenReturn(true);

        assertThrows(ConflictException.class, () -> authService.register(validRequest));

        verify(userRepository, never()).guardar(any(User.class));
        verify(passwordHasher, never()).hash(anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyMap());
    }
}
