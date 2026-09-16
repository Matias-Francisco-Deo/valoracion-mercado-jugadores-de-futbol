package com.overcode.service.impl;

import com.overcode.controller.dto.auth.AuthResponse;
import com.overcode.model.User;
import com.overcode.model.security.Auth;
import com.overcode.security.UserAuth;
import com.overcode.service.exception.AuthenticationException;
import com.overcode.service.exception.ConflictException;
import com.overcode.service.interfaces.AuthService;
import com.overcode.service.interfaces.UserService;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserService userService;
    private final UserAuth userAuth;

    public AuthServiceImpl(UserService userService, UserAuth userAuth) {
        this.userService = userService;
        this.userAuth = userAuth;
    }

    @Override
    public Auth register(User user) {
        log.info("Attempting to register user with email: {}", user.getEmail());
        if (userService.existePorEmail(user.getEmail())) {
            log.warn("Registration failed: Email already registered: {}", user.getEmail());
            throw new ConflictException("Email already registered");
        }
        userAuth.register(user);
        User saved = userService.guardar(user);
        log.info("User registered successfully with ID: {}", saved.getId());

        return userAuth.login(user);
    }

    @Override
    public Auth login(User user) {
        log.info("Attempting login for email: {}", user.getEmail());
        Optional<User> userOptional = userService.recuperarPorEmail(user.getEmail());
        if (userOptional.isEmpty()) {
            log.warn("Login failed: User not found for email: {}", user.getEmail());
            throw new AuthenticationException("Invalid credentials");
        }
        User persistedUser = userOptional.get();
        userAuth.validateAuthenticated(user, persistedUser);

        return userAuth.login(persistedUser);
    }
}
