package com.overcode.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public abstract class Order {

    protected Long id;
    protected Long userId;
    protected Long playerId;
    protected Instant createdAt;

    public Order() {
        this.createdAt = Instant.now();
    }

    public Order(Long id, Long userId, Long playerId, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.playerId = playerId;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }
}
