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
    private String league;

    private Integer goals;
    private Integer assists; // no se muestra
    private Integer shotsOnTarget;
    private Integer passes;

    private Integer interceptions;
    private Integer tackles;
    private Integer keyPasses; // no se muestra
    private Double rating;

    private Integer successfulDribbles;

    private List<Token> tokens;

    public Player(Long id, String name, String clubName, String league,
                  Integer goals,
                  Integer shotsOnTarget, Integer passes,
                  Integer interceptions, Integer keyPasses,
                  Integer assists, Integer tackles,
                  Double rating, Integer successfulDribbles) {
        setId(id);
        setName(name);
        setLeague(league);
        setClubName(clubName);

        setAssists(assists);
        setTackles(tackles);
        setGoals(goals);
        setShotsOnTarget(shotsOnTarget);
        setPasses(passes);
        setInterceptions(interceptions);
        setKeyPasses(keyPasses);
        setRating(rating);
        setSuccessfulDribbles(successfulDribbles);
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

    public Player(String name, String clubName, String league,
                  Integer goals,
                  Integer shotsOnTarget, Integer passes,
                  Integer interceptions, Integer keyPasses,
                  Integer assists, Integer tackles,
                  Double rating, Integer successfulDribbles) {
        this(null, name, clubName, league, goals, shotsOnTarget, passes, interceptions, keyPasses, assists, tackles, rating, successfulDribbles);
    }


    public void setCurrentPrice(Integer currentPrice) {
        this.currentPrice = Math.max(currentPrice, 1);
    }
}
