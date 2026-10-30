package com.overcode.service.interfaces;

import com.overcode.model.security.Auth;
import com.overcode.model.user.User;

public interface AuthService {
    Auth register(User user);
    Auth login(User user);
}
