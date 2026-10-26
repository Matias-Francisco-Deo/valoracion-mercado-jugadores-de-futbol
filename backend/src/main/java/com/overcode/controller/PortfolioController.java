package com.overcode.controller;

import com.overcode.controller.dto.portfolio.DepositRequest;
import com.overcode.controller.dto.portfolio.DepositResponseDTO;
import com.overcode.controller.dto.portfolio.PortfolioResponseDTO;
import com.overcode.model.Portfolio;
import com.overcode.model.User;
import com.overcode.service.interfaces.PortfolioService;
import com.overcode.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/portfolio")
@Tag(name = "Portfolio", description = "Endpoints for a user's portfolio and credit balance")
public class PortfolioController {

    private static final String ERROR_KEY = "error";

    private final PortfolioService portfolioService;
    private final UserService userService;

    public PortfolioController(PortfolioService portfolioService, UserService userService) {
        this.portfolioService = portfolioService;
        this.userService = userService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<Object> deposit(@Valid @RequestBody DepositRequest request, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of(ERROR_KEY, "Unauthorized"));
        }

        User user = userService.recuperarPorEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        try {
            User updatedUser = userService.depositarCreditos(user.getId(), request.getAmount().doubleValue());
            return ResponseEntity.ok(DepositResponseDTO.of(updatedUser.getCreditBalance()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(ERROR_KEY, e.getMessage()));
        }
        // PortfolioNotFoundException is mapped globally by GlobalExceptionHandler (404).
    }

    @GetMapping
    public ResponseEntity<Object> getPortfolio(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of(ERROR_KEY, "Unauthorized"));
        }

        User user = userService.recuperarPorEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        Portfolio portfolio = portfolioService.getByUserId(user.getId());
        return ResponseEntity.ok(PortfolioResponseDTO.from(portfolio));
        // PortfolioNotFoundException is mapped globally by GlobalExceptionHandler (404).
    }
}
