package com.overcode.model.player;

import com.overcode.model.Token;
import com.overcode.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Player {

    private Long id;
    private Long externalId;

    private String name;
    private Integer currentPrice;
    private List<Token> tokens;

    private Team team;

    private PlayerGameData playerGameData;

    public Player() {
        setCurrentPrice(1);
        setTokens(getInitialTokens());
    }

    public Player(Long id,
                  Long externalId,
                  String name,
                  Team team,
                  PlayerGameData playerGameData)
    {
        setId(id);
        setExternalId(externalId);
        setName(name);
        setTeam(team);
        setPlayerGameData(playerGameData);
        setCurrentPrice(1);
        setTokens(getInitialTokens());

    }

    public Player(String name, Team team, PlayerGameData playerGameData) {
        this(null, null, name, team, playerGameData);
    }

    public Player(String name, Long externalId, PlayerGameData playerGameData) {
        this(null, externalId, name, null, playerGameData);
    }

    private List<Token> getInitialTokens() {
        List<Token> newTokens = new java.util.ArrayList<>();
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

    public void recalcularCotizacion(Double cotizacion) {
        setCurrentPrice(cotizacion.intValue());
    }

    public void assignInitialTokensTo(User owner) {
        if (this.tokens != null && owner != null) {
            owner.addTokens(this.tokens);
        }
    }
}
