package com.overcode.persistence.repository.dao.external.scrapper.whoscored;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.overcode.model.Player;
import com.overcode.persistence.dto.external.PlayerDraftDTO;
import com.overcode.persistence.repository.dao.external.scrapper.exception.ScraperExtractionException;
import com.overcode.persistence.repository.dao.external.scrapper.http.ScraperHttpClient;
import com.overcode.persistence.repository.dao.external.scrapper.util.JsonExtractorUtil;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class ExternalPlayerWhoScoredScrapper {

    /*
    * 2 (Premier League)

        4 (LaLiga)

        5 (Serie A)

        3 (Bundesliga)

        22 (Ligue 1)
    * */
    private static final Set<Integer> TOP_5_LEAGUES_IDS = Set.of(2, 4, 5, 3, 22);

    private final ScraperHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ExternalPlayerWhoScoredScrapper(ScraperHttpClient httpClient) {
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        this.objectMapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        this.objectMapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS, true);
    }

    public Optional<Player> getDatosDeJugador(Long playerId, PlayerDraftDTO playerDraftDTO) {
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
            int totalWasDribbled = 0;
            int totalSuccessfulDribbles = 0;
            int totalGamesPlayed = 0;

            double totalPasses = 0.0;
            double totalAccuratePasses = 0.0;

            double sumRating = 0.0;
            int ratingAppsCount = 0;

            // 2. TODOS los torneos
            for (JsonNode tournament : tournaments) {
                // Si NO es de una liga Top 5, saltamos y seguimos buscando
                if (!TOP_5_LEAGUES_IDS.contains(tournament.path("TournamentId").asInt(-1))) {
                    continue;
                }

                // Si ES, sumamos a los totales
                totalGoals += tournament.path("Goals").asInt(0);
                totalAssists += tournament.path("Assists").asInt(0);
                totalShotsOnTarget += tournament.path("ShotsOnTarget").asInt(0);
                totalInterceptions += tournament.path("Interceptions").asInt(0);
                totalTackles += tournament.path("TotalTackles").asInt(0);
                totalKeyPasses += tournament.path("KeyPasses").asInt(0);
                totalWasDribbled += tournament.path("WasDribbled").asInt(0);
                totalSuccessfulDribbles += tournament.path("Dribbles").asInt(0);

                int apps = tournament.path("GameStarted").asInt(0) + tournament.path("SubOn").asInt(0);
                totalGamesPlayed += apps;

                totalPasses += tournament.path("TotalPasses").asDouble(0.0);
                totalAccuratePasses += tournament.path("AccuratePasses").asDouble(0.0);

                // Para el Rating hacemos un promedio ponderado según los partidos jugados en ESE equipo/liga
                double rating = tournament.path("Rating").asDouble(0.0);
                if (rating > 0 && apps > 0) {
                    sumRating += (rating * apps);
                    ratingAppsCount += apps;
                }
            }

            // 3. Si terminó de revisar todos y no sumó ni 1 partido, lanzamos el error
            if (totalGamesPlayed == 0) {
                throw new ScraperExtractionException("El jugador no registra actividad en ninguna de las 5 ligas principales.");
            }

            // 4. Cálculos finales de porcentajes y promedios
            double finalRating = ratingAppsCount > 0 ? (sumRating / ratingAppsCount) : 0.0;
            double passSuccess = totalPasses > 0 ? (totalAccuratePasses / totalPasses) * 100 : 0.0;

            // 5. Guardamos en el DTO
            Player player = new Player();
            player.setName(playerDraftDTO.name());
            player.setClubName(playerDraftDTO.clubName());
            player.setCurrentPrice(1);
//            metrics.setPlayerId(playerId);
            player.setGoals(totalGoals);
            player.setAssists(totalAssists);
            player.setShotsOnTarget(totalShotsOnTarget);
            player.setPasses((int) Math.round(passSuccess));
            player.setInterceptions(totalInterceptions);
            player.setTackles(totalTackles);
            player.setKeyPasses(totalKeyPasses);
//            player.setWasDribbled(totalWasDribbled);
            player.setSuccessfulDribbles(totalSuccessfulDribbles);
//            player.setGamesPlayed(totalGamesPlayed);
            player.setRating(Math.round(finalRating * 100.0) / 100.0);

            return Optional.of(player);

        } catch (JsonProcessingException e) {
            throw new ScraperExtractionException("Error al parsear el JSON de estadísticas con Jackson.", e);
        }
    }
}
