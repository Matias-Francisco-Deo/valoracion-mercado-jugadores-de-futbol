package com.overcode.service.interfaces;

import com.overcode.controller.dto.AuthDtos.AuthResponse;

public interface AuthService {
    AuthResponse register(String username, String email, String password);
    AuthResponse login(String email, String password);
}
