package com.overcode.controller.dto.auth;

import com.overcode.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "El email es obligatorio") @Email(message = "El email debe ser valido")
        @Pattern(regexp = "^$|^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "El email contiene caracteres invalidos")
        String email,
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, max = 30, message = "La contraseña debe tener entre 3 y 30 caracteres")
        String password
) {
    public User aModelo() {
        return new User("placeholder", email, password);
    }
}
