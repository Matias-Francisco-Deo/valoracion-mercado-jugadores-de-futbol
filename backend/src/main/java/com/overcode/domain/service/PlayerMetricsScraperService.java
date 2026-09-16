package com.overcode.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.overcode.infrastructure.scraper.exception.ScraperExtractionException;
import com.overcode.infrastructure.scraper.http.ScraperHttpClient;
import com.overcode.infrastructure.scraper.util.JsonExtractorUtil;
import com.overcode.persistence.dto.WeeklyMetrics;
import org.springframework.stereotype.Service;

@Service
public class PlayerMetricsScraperService {

    private final ScraperHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public PlayerMetricsScraperService(ScraperHttpClient httpClient) {
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        this.objectMapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        this.objectMapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS, true);
    }

    /**
     * Extrae las estadísticas detalladas (Opta) del jugador desde el JSON embebido en la página.
     * 
     * @param playerId ID interno de WhoScored
     * @return DTO con las métricas requeridas
     */
    public WeeklyMetrics extractWeeklyMetrics(Long playerId) {
        String playerUrl = "https://www.whoscored.com/players/" + playerId + "/show/";
        String html = httpClient.getHtml(playerUrl);

        // Aislamos el JSON crudo del estado inicial de la página
        String rawJson = JsonExtractorUtil.extractPlayerStatsJson(html);

        try {
            JsonNode rootNode = objectMapper.readTree(rawJson);
            
            JsonNode tournaments = rootNode.path("tournaments");
            if (tournaments.isMissingNode() || !tournaments.isArray()) {
                 throw new ScraperExtractionException("No se encontró el nodo 'tournaments' en el JSON.");
            }

            int totalGoals = 0;
            int totalAssists = 0;
            int totalShotsOnTarget = 0;
            int totalInterceptions = 0;
            int totalTackles = 0;
            int totalKeyPasses = 0;
            int totalGamesPlayed = 0;
            
            double totalPasses = 0;
            double totalAccuratePasses = 0;
            
            double sumRating = 0.0;
            int ratingAppsCount = 0;

            // Agregamos métricas de todos los torneos oficiales provistos por Opta
            // si queremos dividir por torneo debemos pasar a usar Jsoup
            for (JsonNode tournament : tournaments) {
                totalGoals += tournament.path("Goals").asInt(0);
                totalAssists += tournament.path("Assists").asInt(0);
                totalShotsOnTarget += tournament.path("ShotsOnTarget").asInt(0);
                totalInterceptions += tournament.path("Interceptions").asInt(0);
                totalTackles += tournament.path("TotalTackles").asInt(0);
                totalKeyPasses += tournament.path("KeyPasses").asInt(0);
                
                // Sumamos Partidos de Titular (GameStarted) y Suplente (SubOn)
                int apps = tournament.path("GameStarted").asInt(0) + tournament.path("SubOn").asInt(0);
                totalGamesPlayed += apps;
                
                totalPasses += tournament.path("TotalPasses").asDouble(0.0);
                totalAccuratePasses += tournament.path("AccuratePasses").asDouble(0.0);
                
                double rating = tournament.path("Rating").asDouble(0.0);
                if (rating > 0 && apps > 0) {
                    sumRating += (rating * apps);
                    ratingAppsCount += apps;
                }
            }

            // Promedio ponderado por la cantidad de partidos (apps) jugados en cada torneo
            double finalRating = ratingAppsCount > 0 ? (sumRating / ratingAppsCount) : 0.0;
            double passSuccess = totalPasses > 0 ? (totalAccuratePasses / totalPasses) * 100 : 0.0;

            WeeklyMetrics metrics = new WeeklyMetrics();
            metrics.setPlayerId(playerId);
            metrics.setGoals(totalGoals);
            metrics.setAssists(totalAssists);
            metrics.setShotsOnTarget(totalShotsOnTarget);
            metrics.setPasses((int) Math.round(passSuccess));
            metrics.setInterceptions(totalInterceptions);
            metrics.setTackles(totalTackles);
            metrics.setKeyPasses(totalKeyPasses);
            metrics.setGamesPlayed(totalGamesPlayed);
            metrics.setRating(Math.round(finalRating * 100.0) / 100.0); // Redondear a 2 decimales

            return metrics;

        } catch (JsonProcessingException e) {
            throw new ScraperExtractionException("Error al parsear el JSON de estadísticas con Jackson.", e);
        }
    }
}
