package com.overcode.model.token;

import com.overcode.model.player.Player;
import com.overcode.model.user.User;
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

    public void ponerALaVenta() {
        this.owner = null;
    }
}
