package com.overcode.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.NonNull;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Player {

    private Long id;
    private String name;
    private Integer currentPrice;
    private List<Token> tokens;

    public Player(Long id, String name) {
        this.id = id;
        this.name = name;
        setCurrentPrice(1);
        setTokens(getInitialTokens()); // deberían ser 100 tokens acá
    }

    private List<Token> getInitialTokens() {
        List<Token> tokens = new java.util.ArrayList<>(List.of());
        for (int i = 0; i < 100; i++) {
            tokens.add(new Token(this));
        }
        return tokens;
    }

    public Player(String name) {
        this(null, name);
    }

    public Player(Long id, String name, Integer currentPrice, List<Token> tokens) {
        this.id = id;
        this.name = name;
        setCurrentPrice(currentPrice);
        setTokens(tokens);
    }

    public void setCurrentPrice(Integer currentPrice) {
        this.currentPrice = Math.max(currentPrice, 1);
    }

}
