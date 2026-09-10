package com.overcode.controller.dto.user;

import com.overcode.model.User;

public record UserResponseDTO(Long id, String username, String email, Integer creditBalance) {
    public static UserResponseDTO desdeModelo(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponseDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getCreditBalance()
        );
    }
}
