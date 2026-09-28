package com.overcode.service.interfaces;

import com.overcode.model.User;
import com.overcode.model.security.Auth;

public interface AuthService {
    Auth register(User user);
    Auth login(User user);
}
