package com.overcode.service.impl;

import com.overcode.controller.dto.auth.AuthResponse;
import com.overcode.controller.dto.user.UserResponseDTO;
import com.overcode.model.User;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.security.JwtUtil;
import com.overcode.security.PasswordHasher;
import com.overcode.service.exception.AuthenticationException;
import com.overcode.service.exception.ConflictException;
import com.overcode.service.interfaces.AuthService;
import com.overcode.service.interfaces.UserService;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordHasher passwordHasher;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, UserService userService, PasswordHasher passwordHasher, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordHasher = passwordHasher;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthResponse register(User user) {
        log.info("Attempting to register user with email: {}", user.getEmail());
        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("Registration failed: Email already registered: {}", user.getEmail());
            throw new ConflictException("Email already registered");
        }
        String hashed = passwordHasher.hash(user.getPassword());// TODO esta lógica no parece de service
        user.setPassword(hashed);
        User saved = userService.guardar(user);
        log.info("User registered successfully with ID: {}", saved.getId());

        Map<String, Object> claims = new HashMap<>();//TODO revisar hash map
        claims.put("uid", saved.getId());
        claims.put("username", saved.getUsername());

        String token = jwtUtil.generateToken(saved.getEmail(), claims);
        var expires = jwtUtil.getExpiration(token).toInstant();
        log.debug("JWT generated for user ID: {}", saved.getId());
        return new AuthResponse(token, expires, UserResponseDTO.desdeModelo(saved));
    }

    @Override
    public AuthResponse login(User userRequest) {
        log.info("Attempting login for email: {}", userRequest.getEmail());
        Optional<User> optionalUser = userRepository.findByEmail(userRequest.getEmail());
        if (optionalUser.isEmpty()) {
            log.warn("Login failed: User not found for email: {}", userRequest.getEmail());
            throw new AuthenticationException("user no existe");
        }
        User user = optionalUser.get();
        if (!passwordHasher.matches(userRequest.getPassword(), user.getPassword())) { // TODO esta lógica no parece de service
            log.warn("Login failed: Invalid password for email: {}", user.getEmail());
            throw new AuthenticationException("Invalid credentials");
        }

        Map<String, Object> claims = new HashMap<>();//TODO revisar hash map
        claims.put("uid", user.getId());
        claims.put("username", user.getUsername());
        String token = jwtUtil.generateToken(user.getEmail(), claims);
        var expires = jwtUtil.getExpiration(token).toInstant();
        
        log.info("User logged in successfully: {}", user.getId());
        return new AuthResponse(token, expires, UserResponseDTO.desdeModelo(user));
    }
}
