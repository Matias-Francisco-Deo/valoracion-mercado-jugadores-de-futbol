package com.overcode.model.market.orden;

import com.overcode.model.player.Player;
import com.overcode.model.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public abstract class Orden {

    protected Long id;
    protected User owner;
    protected Player player;
    protected Instant createdAt;

    protected Orden() {
        this.createdAt = Instant.now();
    }

    protected Orden(Long id, User owner, Player player, Instant createdAt) {
        this.id = id;
        this.owner = owner;
        this.player = player;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }
}
