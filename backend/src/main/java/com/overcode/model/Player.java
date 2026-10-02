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

    private Team team;

    private PlayerGameData playerGameData;




    public Player(Long id,
                  Long externalId,
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

    public Player(String name, Team team, PlayerGameData playerGameData) {
        this(null, null, name, team, playerGameData);
    }

    private List<Token> getInitialTokens() {
        List<Token> newTokens = new java.util.ArrayList<>(List.of());
        for (int i = 0; i < 100; i++) {
            newTokens.add(new Token(this));
        }
        return newTokens;
    }

    public Player(String name,
                  Long externalId,
                  Team team,
                  PlayerGameData playerGameData) {
        this(null, externalId, name, team, playerGameData);
    }


    public void setCurrentPrice(Integer currentPrice) {
        this.currentPrice = Math.max(currentPrice, 1);
    }
}
