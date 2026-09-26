package com.overcode.controller.dto.user;

import com.overcode.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserRequestDTO(
    @NotBlank(message = "Username is required") String username, // TODO testear esto, trimmear?
    @NotBlank(message = "Email is required") @Email(message = "Email must be valid") String email,
    @NotBlank(message = "Password is required") String password
) {
    public User aModelo() {
        String trimmedUsername = username.trim();
        return new User(trimmedUsername, email, password);
    }
}
