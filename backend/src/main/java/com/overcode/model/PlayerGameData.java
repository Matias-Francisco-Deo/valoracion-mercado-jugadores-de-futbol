package com.overcode.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlayerGameData {
    private Long id;

    private Player player;

    private Integer goals;
    private Integer assists; // no se muestra
    private Integer shotsOnTarget;
    private Integer keyPasses; // no se muestra

    private Integer tackles;
    private Integer successfulDribbles;
    private Double rating;

    public PlayerGameData(Long id, Integer goals, Integer assists,
                          Integer shotsOnTarget, Integer keyPasses,
                         Integer tackles, Integer successfulDribbles,
                          Double rating) {
        setId(id);
        setGoals(goals);
        setAssists(assists);
        setShotsOnTarget(shotsOnTarget);
        setKeyPasses(keyPasses);
        setTackles(tackles);
        setSuccessfulDribbles(successfulDribbles);
        setRating(rating);
    }

    public PlayerGameData(Integer goals, Integer assists,
                          Integer shotsOnTarget, Integer keyPasses,
                          Integer tackles, Integer successfulDribbles,
                          Double rating) {
        this(null, goals, assists, shotsOnTarget, keyPasses, tackles, successfulDribbles, rating);
    }
}

