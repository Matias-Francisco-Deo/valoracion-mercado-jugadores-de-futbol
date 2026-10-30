package com.overcode.model.player;

import com.overcode.model.exception.PosicionNoValidaException;

public enum PlayerPosition {
    DEFENSOR, ATACANTE, CENTROCAMPISTA, ARQUERO;

    private static final String GOALKEEPER = "GOALKEEPER";
    private static final String DEFENDER = "DEFENDER";
    private static final String MIDFIELDER = "MIDFIELDER";
    private static final String FORWARD = "FORWARD";

    public static PlayerPosition aModelo(String position) {
        position = position.toUpperCase();
        if (position.contains(DEFENDER)) {
            return DEFENSOR;
        } else if (position.contains(FORWARD)) {
            return ATACANTE;
        } else if (position.contains(MIDFIELDER)) {
            return CENTROCAMPISTA;
        } else if (position.contains(GOALKEEPER)) {
            return ARQUERO;
        }
        throw new PosicionNoValidaException("Posición no válida: " + position);
    }
}
