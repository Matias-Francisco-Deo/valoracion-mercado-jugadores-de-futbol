package com.overcode.security;

import com.overcode.controller.dto.auth.AuthResponse;
import com.overcode.controller.dto.user.UserResponseDTO;
import com.overcode.model.User;
import com.overcode.model.security.Auth;
import com.overcode.service.exception.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserAuth {
    private final PasswordHasher passwordHasher;
    private final JwtUtil jwtUtil;

    private static final Logger log = LoggerFactory.getLogger(UserAuth.class);

    public UserAuth(PasswordHasher passwordHasher, JwtUtil jwtUtil) {
        this.passwordHasher = passwordHasher;
        this.jwtUtil = jwtUtil;
    }

    public void register(User user) {
        String hashed = passwordHasher.hash(user.getPassword());
        user.setPassword(hashed);
    }

    public void validateAuthenticated(User user, User persistedUser) {
        if (!passwordHasher.matches(user.getPassword(), persistedUser.getPassword())) {
            log.warn("Login failed: Invalid password for email: {}", user.getEmail());
            throw new AuthenticationException("Invalid credentials");
        }
    }

    public Auth login(User user) {
        AuthClaims claims = new AuthClaims(user.getId(), user.getUsername());

        String token = jwtUtil.generateToken(user.getEmail(), claims);
        var expires = jwtUtil.getExpiration(token).toInstant();
        log.debug("JWT generated for user ID: {}", user.getId());
        return new Auth(token, expires, user);
    }
}
