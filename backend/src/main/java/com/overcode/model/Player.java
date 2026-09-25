package com.overcode.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Player {

    private Long id;
    private Long externalId;

    private String name;
    private Integer currentPrice;
    private String clubName;

    private Integer goals;
    private Integer assists; // no
    private Integer shotsOnTarget;
    private Integer passes;

    private Integer interceptions;
    private Integer tackles;
    private Integer keyPasses; // no
    private Double rating;

//    private Integer wasDribbled;
    private Integer successfulDribbles;
//    private Integer gamesPlayed; TODO pal futuro?

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

//    public void actualizarMetricas(WeeklyMetrics newMetrics) {
//        if (newMetrics != null) {
//            this.metrics = newMetrics;
//        }
//    }
}
