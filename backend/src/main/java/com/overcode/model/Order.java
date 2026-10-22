package com.overcode.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public abstract class Order {

    protected Long id;
    protected User owner;
    protected Player player;
    protected Instant createdAt;

    protected Order() {
        this.createdAt = Instant.now();
    }

    protected Order(Long id, User owner, Player player, Instant createdAt) {
        this.id = id;
        this.owner = owner;
        this.player = player;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }
}
