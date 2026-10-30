package com.overcode.controller.dto.bonus;

import com.overcode.model.user.User;

public record DailyBonusRequestDTO(Long userId) {


    public User aModelo() {
        return new User(userId);
    }
}
