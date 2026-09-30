package com.overcode.controller.dto.auth;

import com.overcode.controller.dto.user.UserResponseDTO;
import com.overcode.model.security.Auth;

import java.time.Instant;

public record AuthResponse(String token, Instant expiresAt, UserResponseDTO user) {
    public static AuthResponse desdeModelo(Auth auth) {
        return new AuthResponse(
            auth.token(),
            auth.expiresAt(),
            UserResponseDTO.desdeModelo(auth.user())
        );
    }
}
