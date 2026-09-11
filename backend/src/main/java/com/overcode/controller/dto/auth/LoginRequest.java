package com.overcode.controller.dto.auth;

import com.overcode.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Email is required") @Email(message = "Email must be valid") String email,
        @NotBlank(message = "Password is required") String password
) {
    public User aModelo() {
        return new User("placeholder", email, password);
    }
}
