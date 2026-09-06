package com.overcode.service.interfaces;

import com.overcode.controller.dto.AuthDtos.AuthResponse;
import com.overcode.controller.dto.AuthDtos.RegisterRequest;
import com.overcode.controller.dto.AuthDtos.LoginRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
