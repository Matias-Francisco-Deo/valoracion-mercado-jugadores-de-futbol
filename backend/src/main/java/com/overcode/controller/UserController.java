package com.overcode.controller;

import com.overcode.controller.dto.CreateUserRequest;
import com.overcode.controller.dto.PortfolioDto;
import com.overcode.controller.dto.TransactionDto;
import com.overcode.controller.dto.UserDto;
import com.overcode.service.interfaces.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @GetMapping("/users/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @GetMapping("/users/{id}/portfolio")
    public PortfolioDto getPortfolio(@PathVariable Long id) {
        return userService.getPortfolio(id);
    } // TODO este dto?

    @GetMapping("/users/{id}/transactions")
    public List<TransactionDto> getTransactions(@PathVariable Long id) {
        return userService.getTransactions(id);
    }
}
