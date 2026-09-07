package com.overcode.service.impl;

import com.overcode.controller.dto.AuthDtos.AuthResponse;
import com.overcode.controller.dto.AuthDtos.RegisterRequest;
import com.overcode.controller.dto.AuthDtos.LoginRequest;
import com.overcode.controller.dto.UserResponseDTO;
import com.overcode.model.User;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.security.JwtUtil;
import com.overcode.security.PasswordHasher;
import com.overcode.service.exception.AuthenticationException;
import com.overcode.service.exception.ConflictException;
import com.overcode.service.interfaces.AuthService;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {//TODO mover request a controller de metodos

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, PasswordHasher passwordHasher, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {//TODO revisar metodo
        log.info("Attempting to register user with email: {}", request.email());
        if (userRepository.existsByEmail(request.email())) {
            log.warn("Registration failed: Email already registered: {}", request.email());
            throw new ConflictException("Email already registered");
        }
        String hashed = passwordHasher.hash(request.password());
        User user = new User(null, request.username(), request.email(), hashed, 0, 0);
        User saved = userRepository.save(user);
        log.info("User registered successfully with ID: {}", saved.getId());

        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", saved.getId());
        claims.put("username", saved.getUsername());

        String token = jwtUtil.generateToken(saved.getEmail(), claims);
        var expires = jwtUtil.getExpiration(token).toInstant();
        log.debug("JWT generated for user ID: {}", saved.getId());
        return new AuthResponse(token, expires, UserResponseDTO.desdeModelo(saved));
    }

    @Override
    public AuthResponse login(LoginRequest request) {//TODO revisar metodo
        log.info("Attempting login for email: {}", request.email());
        Optional<User> maybe = userRepository.findByEmail(request.email());
        if (maybe.isEmpty()) {
            log.warn("Login failed: User not found for email: {}", request.email());
            throw new AuthenticationException("Invalid credentials");
        }
        User user = maybe.get();
        if (!passwordHasher.matches(request.password(), user.getPassword())) {
            log.warn("Login failed: Invalid password for email: {}", request.email());
            throw new AuthenticationException("Invalid credentials");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", user.getId());
        claims.put("username", user.getUsername());
        String token = jwtUtil.generateToken(user.getEmail(), claims);
        var expires = jwtUtil.getExpiration(token).toInstant();
        
        log.info("User logged in successfully: {}", user.getId());
        return new AuthResponse(token, expires, UserResponseDTO.desdeModelo(user));
    }
}
