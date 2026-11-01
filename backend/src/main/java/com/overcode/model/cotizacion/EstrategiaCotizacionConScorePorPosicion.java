package com.overcode.model.cotizacion;

import com.overcode.model.player.Player;
import com.overcode.model.player.PlayerGameData;
import com.overcode.model.player.PlayerPosition;

public abstract class EstrategiaCotizacionConScorePorPosicion extends EstrategiaCotizacion {

    protected EstrategiaCotizacionConScorePorPosicion(String titulo, Double valorBase, Double factorEscala) {
        setTitulo(titulo);
        setValorBase(valorBase);
        setFactorEscala(factorEscala);
    }

    @Override
    public Double calcularScore(Player player) {
        PlayerGameData playerGameData = player.getPlayerGameData();
        PlayerPosition posicion = playerGameData.getPosition();

        return switch (posicion) {
            case ATACANTE -> getScoreParaAtacante(playerGameData);
            case CENTROCAMPISTA -> getScoreParaCentrocampista(playerGameData);
            case DEFENSOR -> getScoreParaDefensor(playerGameData);
            case ARQUERO -> getScoreParaArquero(playerGameData);
        } + getScoreGeneral(playerGameData)
        ;
    }

    abstract Double getScoreGeneral(PlayerGameData playerGameData);

    abstract Double getScoreParaAtacante(PlayerGameData playerGameData);

    abstract Double getScoreParaCentrocampista(PlayerGameData playerGameData);

    abstract Double getScoreParaDefensor(PlayerGameData playerGameData);

    abstract Double getScoreParaArquero(PlayerGameData playerGameData);


}
