package com.overcode.controller;

import com.overcode.controller.dto.auth.AuthResponseDTO;
import com.overcode.controller.dto.auth.LoginRequestDTO;
import com.overcode.controller.dto.auth.RegisterRequestDTO;
import com.overcode.model.security.Auth;
import com.overcode.service.interfaces.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        Auth auth = authService.register(request.aModelo());
        AuthResponseDTO response = AuthResponseDTO.desdeModelo(auth);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Log in", description = "Authenticates a user and returns a JWT token")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        Auth auth = authService.login(request.aModelo());
        AuthResponseDTO response = AuthResponseDTO.desdeModelo(auth);
        return ResponseEntity.ok(response);
    }
}
