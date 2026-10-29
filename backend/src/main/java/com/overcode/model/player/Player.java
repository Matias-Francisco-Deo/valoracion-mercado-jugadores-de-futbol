package com.overcode.model.player;

import com.overcode.model.token.Token;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class Player {

    private Long id;
    private Long externalId;

    private String name;
    private Double currentPrice;
    private List<Token> tokens;

    private Team team;

    private PlayerGameData playerGameData;

    public Player() {
        setCurrentPrice(1d);
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
        setCurrentPrice(1d);
        setTokens(getInitialTokens());

    }

    public Player(String name, Team team, PlayerGameData playerGameData) {
        this(null, null, name, team, playerGameData);
    }

    public Player(String name, Long externalId, PlayerGameData playerGameData) {
        this(null, externalId, name, null, playerGameData);
    }

    public Player(Long id){//constructor para placeholder de request
        setId(id);
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

    public List<Token> getTokensFrom(List<Token> tokensToFind) {
        Set<Long> tokensIds = tokensToFind.stream().map(Token::getId).collect(Collectors.toSet());

        return this.tokens.stream().filter(token -> tokensIds.contains(token.getId())).toList();
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = Math.floor(Math.max(currentPrice, 1) * 100) / 100;
    }

    public void recalcularCotizacion(Double cotizacion) {
        setCurrentPrice(cotizacion);
    }
}
