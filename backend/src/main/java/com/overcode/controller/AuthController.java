package com.overcode.controller;

import com.overcode.controller.dto.auth.AuthResponse;
import com.overcode.controller.dto.auth.LoginRequest;
import com.overcode.controller.dto.auth.RegisterRequest;
import com.overcode.model.security.Auth;
import com.overcode.service.interfaces.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account and returns a JWT token")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        Auth auth = authService.register(request.aModelo());
        AuthResponse response = AuthResponse.desdeModelo(auth);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Log in", description = "Authenticates a user and returns a JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Auth auth = authService.login(request.aModelo());
        AuthResponse response = AuthResponse.desdeModelo(auth);
        return ResponseEntity.ok(response);
    }
}
