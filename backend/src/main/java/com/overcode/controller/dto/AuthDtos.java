package com.overcode.controller.dto;

import com.overcode.controller.dto.user.UserResponseDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

public class AuthDtos {
//TODO separar dtos
    public record RegisterRequest(
        @NotBlank(message = "Username is required") String username,
        @NotBlank(message = "Email is required") @Email(message = "Email must be valid") String email,
        @NotBlank(message = "Password is required") String password
    ) { }

    public record LoginRequest(
        @NotBlank(message = "Email is required") @Email(message = "Email must be valid") String email,
        @NotBlank(message = "Password is required") String password
    ) { }

    public record AuthResponse(String token, Instant expiresAt, UserResponseDTO user) { }
}
