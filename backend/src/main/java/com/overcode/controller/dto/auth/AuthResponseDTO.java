package com.overcode.controller.dto.auth;

import com.overcode.controller.dto.user.UserResponseDTO;
import com.overcode.model.security.Auth;

import java.time.Instant;

public record AuthResponseDTO(String token, Instant expiresAt, UserResponseDTO user) {
    public static AuthResponseDTO desdeModelo(Auth auth) {
        return new AuthResponseDTO(
            auth.token(),
            auth.expiresAt(),
            UserResponseDTO.desdeModelo(auth.user())
        );
    }
}
