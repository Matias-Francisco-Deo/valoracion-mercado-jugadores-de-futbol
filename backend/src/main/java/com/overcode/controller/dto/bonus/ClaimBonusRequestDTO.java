package com.overcode.controller.dto.bonus;

import com.overcode.model.user.User;

public record ClaimBonusRequestDTO(Long userId) {

    public User aModelo() {
        return new User(userId);
    }
}
