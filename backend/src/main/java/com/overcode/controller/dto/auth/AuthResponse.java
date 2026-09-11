package com.overcode.controller.dto.auth;

import com.overcode.controller.dto.user.UserResponseDTO;

import java.time.Instant;

public record AuthResponse(String token, Instant expiresAt, UserResponseDTO user) {
}
