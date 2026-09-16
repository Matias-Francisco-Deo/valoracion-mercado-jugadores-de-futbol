package com.overcode.model.security;

import com.overcode.controller.dto.user.UserResponseDTO;
import com.overcode.model.User;

import java.time.Instant;

public record Auth(String token, Instant expiresAt, User user) {
}
