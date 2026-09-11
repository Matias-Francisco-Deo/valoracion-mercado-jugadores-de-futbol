package com.overcode.controller.dto.user;

import com.overcode.model.Token;
import com.overcode.model.User;

import java.util.List;
import java.util.stream.Collectors;

public record UserResponseDTO(Long id, String username, String email, Integer creditBalance, List<Long> tokens) {
    public static UserResponseDTO desdeModelo(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponseDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getCreditBalance(),
                user.getTokens().stream().map(Token::getId).collect(Collectors.toList())
        );
    }
}
