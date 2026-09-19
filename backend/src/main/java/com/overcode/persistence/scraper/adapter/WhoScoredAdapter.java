package com.overcode.persistence.scraper.adapter;

import com.overcode.persistence.scraper.service.WhoScoredIdResolver;
import com.overcode.service.impl.PlayerMetricsScraperService;
import com.overcode.model.WeeklyMetrics;
import com.overcode.service.interfaces.PlayerMetricsProvider;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class WhoScoredAdapter implements PlayerMetricsProvider {

    private final WhoScoredIdResolver whoScoredIdResolver;
    private final PlayerMetricsScraperService playerMetricsScraperService;

    public WhoScoredAdapter(WhoScoredIdResolver whoScoredIdResolver,
                            PlayerMetricsScraperService playerMetricsScraperService) {
        this.whoScoredIdResolver = whoScoredIdResolver;
        this.playerMetricsScraperService = playerMetricsScraperService;
    }

    /**
     * Orquesta el Flujo 1 (Mapeo) y el Flujo 2 (Métricas).
     * Utiliza la caché de Spring para devolver el último valor conocido si es llamado repetidamente.
     * @param teamName   El nombre del equipo.
     * @param playerName El nombre del jugador.
     * @return Las métricas semanales del jugador.
     */
    @Cacheable(value = "playerMetricsCache", key = "#teamName + '-' + #playerName")
    public WeeklyMetrics getPlayerMetrics(String teamName, String playerName) {
        // Flujo 1: Resolver el ID del jugador
        Long playerId = whoScoredIdResolver.resolvePlayerId(teamName, playerName);
        
        // Flujo 2: Extraer las métricas usando el ID resuelto
        return playerMetricsScraperService.extractWeeklyMetrics(playerId);
    }
}
