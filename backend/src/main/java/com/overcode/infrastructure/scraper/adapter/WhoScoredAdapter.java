package com.overcode.infrastructure.scraper.adapter;

import com.overcode.domain.service.PlayerMappingService;
import com.overcode.domain.service.PlayerMetricsScraperService;
import com.overcode.persistence.dto.WeeklyMetrics;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class WhoScoredAdapter {

    private final PlayerMappingService playerMappingService;
    private final PlayerMetricsScraperService playerMetricsScraperService;

    public WhoScoredAdapter(PlayerMappingService playerMappingService,
                            PlayerMetricsScraperService playerMetricsScraperService) {
        this.playerMappingService = playerMappingService;
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
        Long playerId = playerMappingService.resolvePlayerId(teamName, playerName);
        
        // Flujo 2: Extraer las métricas usando el ID resuelto
        return playerMetricsScraperService.extractWeeklyMetrics(playerId);
    }
}
