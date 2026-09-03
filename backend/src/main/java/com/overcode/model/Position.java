package com.overcode.model;

public class Position {

    private Long id;
    private Long userId;
    private Long playerId;
    private Integer quantity;

    public Position(Long id, Long userId, Long playerId, Integer quantity) {
        if (userId == null) {
            throw new IllegalArgumentException("User id is required");
        }
        if (playerId == null) {
            throw new IllegalArgumentException("Player id is required");
        }
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.id = id;
        this.userId = userId;
        this.playerId = playerId;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = quantity;
    }
}
