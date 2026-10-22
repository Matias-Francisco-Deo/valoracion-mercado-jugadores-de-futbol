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
        User seller = this.owner != null ? this.owner : null;
        Player player = this.player != null ? this.player : null;
        this.owner = null;
        return new SellOrder(seller, player, this, price);
    }
}
