package com.overcode.model.token;

import com.overcode.model.player.Player;
import com.overcode.model.SellOrder;
import com.overcode.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class Token {

    private Long id;
    private User owner;
    private Player player;

    public Token(Player player) {
        setPlayer(player);
    }

    public Token(Long id, User owner, Player player) {
        this.id = id;
        this.owner = owner;
        this.player = player;
    }

    public boolean isInEscrow() {
        return this.owner == null;
    }

    public SellOrder vender(Integer price) {
        Long sellerId = this.owner != null ? this.owner.getId() : null;
        Long pId = this.player != null ? this.player.getId() : null;
        this.owner = null;
        return new SellOrder(sellerId, pId, this.id, price);
    }
}
