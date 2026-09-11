package com.overcode.service.interfaces;

import com.overcode.controller.dto.auth.AuthResponse;
import com.overcode.model.User;

public interface AuthService {
    AuthResponse register(User user);
    AuthResponse login(User user);
}
