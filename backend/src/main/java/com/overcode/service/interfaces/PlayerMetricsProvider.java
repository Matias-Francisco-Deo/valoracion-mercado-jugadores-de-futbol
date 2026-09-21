package com.overcode.service.interfaces;

import com.overcode.model.WeeklyMetrics;

public interface PlayerMetricsProvider {
    /**
     * Obtiene las métricas semanales de un jugador específico.
     * @param clubName El nombre del equipo actual.
     * @param playerName El nombre del jugador.
     * @return Las métricas semanales del jugador o null si no se encuentra.
     */
    WeeklyMetrics getPlayerMetrics(String clubName, String playerName);
}
