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
    private List<Token> tokens;

    // Team -> League

    private Team team;
//    private String clubName;
//    private String league;

    // PlayerGameData?

    private PlayerGameData playerGameData;

//    private Integer goals;
//    private Integer assists; // no se muestra
//    private Integer shotsOnTarget;
//    private Integer passes; // sacar
//    private Integer keyPasses; // no se muestra
//
//    private Integer interceptions; // sacar
//    private Integer tackles;
//    private Integer successfulDribbles;



    public Player(Long id,
                  String name,
                  Team team,
                  PlayerGameData playerGameData)
    {
        setId(id);
        setName(name);
        setTeam(team);
        setPlayerGameData(playerGameData);
        setCurrentPrice(1);
        setTokens(getInitialTokens());

    }

    private List<Token> getInitialTokens() {
        List<Token> newTokens = new java.util.ArrayList<>(List.of());
        for (int i = 0; i < 100; i++) {
            newTokens.add(new Token(this));
        }
        return newTokens;
    }

    public Player(String name,
                  Team team,
                  PlayerGameData playerGameData) {
        this(null, name, team, playerGameData);
    }


    public void setCurrentPrice(Integer currentPrice) {
        this.currentPrice = Math.max(currentPrice, 1);
    }
}
