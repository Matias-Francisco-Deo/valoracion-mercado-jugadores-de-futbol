package com.overcode.controller.dto.auth;

import com.overcode.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank(message = "Username is required") String username,
        @NotBlank(message = "Email is required") @Email(message = "Email must be valid") String email,
        @NotBlank(message = "Password is required") String password
) {
    public User aModelo() {
        String trimmedUsername = username.trim();
        return new User(trimmedUsername, email, password);
    }
}
