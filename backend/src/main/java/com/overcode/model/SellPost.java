package com.overcode.model;

public record SellPost(Long userId, Long playerId, int quantity) {
    public SellPost {
        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        if (playerId == null) {
            throw new IllegalArgumentException("Player ID must not be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }
}
