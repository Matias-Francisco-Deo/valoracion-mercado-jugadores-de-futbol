package com.overcode.model;

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

}
