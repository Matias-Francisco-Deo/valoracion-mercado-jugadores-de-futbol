package com.overcode.controller.dto.auth;

import com.overcode.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank(message = "El Usuario es obligatoria") String username,
        @NotBlank(message = "El Email es obligatoria") @Email(message = "El Email debe ser valido") String email,
        @NotBlank(message = "La Contraseña es obligatoria") String password
) {
    public User aModelo() {
        String trimmedUsername = username.trim();
        return new User(trimmedUsername, email, password);
    }
}
