package com.overcode.model.cotizacion;

import com.overcode.model.player.PlayerGameData;

public class EstrategiaCotizacionRendimientoTotal extends EstrategiaCotizacionConScorePorPosicion {

    public EstrategiaCotizacionRendimientoTotal() {
        super("Rendimiento total en base a todas las métricas", 100.0, 1.5);
    }

    @Override
    Double getScoreGeneral(PlayerGameData playerGameData) {
        return playerGameData.getRating() * 10.0 + playerGameData.getKeyPasses() * 5.0 + playerGameData.getAssists() * 2.0;
    }

    @Override
    Double getScoreParaAtacante(PlayerGameData playerGameData) {
        return (
                playerGameData.getGoals() * 10.0
                        + playerGameData.getShotsOnTarget() * 3.0
                        + playerGameData.getSuccessfulDribbles());
    }

    @Override
    Double getScoreParaCentrocampista(PlayerGameData playerGameData) {
        return playerGameData.getSuccessfulDribbles() * 10.0;
    }

    @Override
    Double getScoreParaDefensor(PlayerGameData playerGameData) {
        return playerGameData.getTackles() * 10.0;
    }

    @Override
    Double getScoreParaArquero(PlayerGameData playerGameData) {
        return 0.0;
    }

}
