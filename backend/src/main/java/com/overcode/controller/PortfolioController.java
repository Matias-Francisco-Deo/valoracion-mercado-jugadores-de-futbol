package com.overcode.controller;

import com.overcode.controller.dto.portfolio.DepositRequest;
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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/portfolio")
@Tag(name = "Portfolio", description = "Endpoints for a user's portfolio and credit balance")
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final UserService userService;

    public PortfolioController(PortfolioService portfolioService, UserService userService) {
        this.portfolioService = portfolioService;
        this.userService = userService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@Valid @RequestBody DepositRequest request, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        User user = userService.recuperarPorEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        try {
            Portfolio portfolio = portfolioService.deposit(user.getId(), request.getAmount());
            return ResponseEntity.ok(Map.of(
                    "message", "Credits deposited successfully",
                    "credits", portfolio.getCredits()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getPortfolio(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        User user = userService.recuperarPorEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        try {
            Portfolio portfolio = portfolioService.getByUserId(user.getId());
            List<Map<String, Object>> holdings = portfolio.getTokenHoldings().stream()
                    .map(h -> Map.<String, Object>of(
                            "playerId", h.getPlayerId(),
                            "quantity", h.getQuantity()))
                    .toList();
            return ResponseEntity.ok(Map.of(
                    "credits", portfolio.getCredits(),
                    "holdings", holdings
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }
}
