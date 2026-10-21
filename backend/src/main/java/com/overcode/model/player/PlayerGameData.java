package com.overcode.model.player;

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
    private PlayerPosition position;


    public PlayerGameData(Long id, Player player,
                          Integer goals, Integer assists,
                          Integer shotsOnTarget, Integer keyPasses,
                         Integer tackles, Integer successfulDribbles,
                          Double rating, PlayerPosition position) {
        setId(id);
        setPosition(position);
        setPlayer(player);
        setGoals(goals);
        setAssists(assists);
        setShotsOnTarget(shotsOnTarget);
        setKeyPasses(keyPasses);
        setTackles(tackles);
        setSuccessfulDribbles(successfulDribbles);
        setRating(rating);
    }

    public PlayerGameData(Player player, Integer goals, Integer assists,
                          Integer shotsOnTarget, Integer keyPasses,
                          Integer tackles, Integer successfulDribbles,
                          Double rating, PlayerPosition position) {
        this(null, player, goals, assists, shotsOnTarget, keyPasses, tackles, successfulDribbles, rating, position);
    }

    public PlayerGameData(Integer goals, Integer assists,
                          Integer shotsOnTarget, Integer keyPasses,
                          Integer tackles, Integer successfulDribbles,
                          Double rating, PlayerPosition position) {
        this(null, null, goals, assists, shotsOnTarget, keyPasses, tackles, successfulDribbles, rating, position);
    }
}

