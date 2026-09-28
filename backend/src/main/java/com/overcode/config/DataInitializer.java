package com.overcode.config;

import com.overcode.service.interfaces.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer {
    private final UserService userService;

    public DataInitializer(
            UserService userService
    ) {this.userService = userService;}

    @PostConstruct
    @Transactional
    public void initialize() {
        userService.crearSuperusuario();
    }
}
