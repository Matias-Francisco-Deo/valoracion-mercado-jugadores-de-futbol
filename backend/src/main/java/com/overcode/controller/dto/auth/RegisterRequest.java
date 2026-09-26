package com.overcode.controller.dto.auth;

import com.overcode.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        @NotBlank(message = "El usuario es obligatorio")
        @Size(min = 3, max = 30, message = "El usuario debe tener entre 3 y 30 caracteres")
        @Pattern(regexp = "^$|^[a-zA-Z0-9_]+$", message = "El usuario solo puede contener letras, números y guion bajo")
        String username,
        @NotBlank(message = "El email es obligatorio") @Email(message = "El email debe ser valido")
        @Pattern(regexp = "^$|^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "El email contiene caracteres invalidos")
        String email,
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, max = 30, message = "La contraseña debe tener entre 3 y 30 caracteres")
        String password
) {
    public User aModelo() {
        String trimmedUsername = username.trim();
        return new User(trimmedUsername, email, password);
    }
}
