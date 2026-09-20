package com.overcode.persistence.repository.dao.external.scraper.service;

import com.overcode.persistence.repository.dao.external.scraper.exception.ScraperExtractionException;
import com.overcode.persistence.repository.dao.external.scraper.http.ScraperHttpClient;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WhoScoredIdResolver {

    public static final String WHOSCORED_URL = "https://www.whoscored.com";
    private final ScraperHttpClient httpClient;

    public WhoScoredIdResolver(ScraperHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Resuelve el ID interno de un jugador en WhoScored a partir del nombre de su equipo y su nombre.
     * 
     * @param teamName   Nombre del equipo (ej. "Barcelona")
     * @param playerName Nombre del jugador (ej. "Lamine Yamal")
     * @return El ID numérico de WhoScored del jugador.
     */
    public Long resolvePlayerId(String teamName, String playerName) {
        // Paso 1: Buscar el equipo en el buscador de WhoScored
        String teamSearchUrl = WHOSCORED_URL + "/Search/?t=" + URLEncoder.encode(teamName, StandardCharsets.UTF_8);
        String searchHtml = httpClient.getHtml(teamSearchUrl);
        
        Long teamId = extractTeamIdFromHtml(searchHtml, teamName);
        
        // Paso 2: Con el Team ID, ir a la página del equipo para buscar al jugador
        // Nota: WhoScored redirige o formatea la URL, pero el ID es suficiente para la ruta base.
        // Ej: https://www.whoscored.com/Teams/65
        String teamUrl = WHOSCORED_URL + "/Teams/" + teamId;
        String teamHtml = httpClient.getHtml(teamUrl);
        
        // Paso 3: Buscar el ID del jugador aislando el href con Regex (sin usar DOM)
        return extractPlayerIdFromHtml(teamHtml, playerName);
    }

    private Long extractTeamIdFromHtml(String html, String teamName) {
        // Normalizamos el nombre del equipo (ej: "Atlético Madrid" -> "atletico-madrid")
        String normalizedTeamName = java.text.Normalizer.normalize(teamName, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replace(" ", "-");
        
        // Normalizamos el HTML para quitarle las tildes a las URLs de WhoScored
        String normalizedHtml = java.text.Normalizer.normalize(html, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        
        // Regex para atrapar: href="/teams/65/show/spain-barcelona"
        // Grupo 1: El ID numérico
        String regex = "href=\"/teams/(\\d+)/show/[^\"]*" + normalizedTeamName + "[^\"]*\"";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(normalizedHtml);
        
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        
        throw new ScraperExtractionException("No se encontró el ID del equipo en el HTML para: " + teamName);
    }

    private Long extractPlayerIdFromHtml(String html, String playerName) {
        // Normalizamos el nombre del jugador (ej. "Enzo Fernández" -> "enzo-fernandez")
        String normalizedPlayerName = java.text.Normalizer.normalize(playerName, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replace(" ", "-");
        
        // Normalizamos el HTML para quitarle las tildes a las URLs de WhoScored
        // (WhoScored pone href="/players/123/show/enzo-fernández", al normalizar queda "enzo-fernandez")
        String normalizedHtml = java.text.Normalizer.normalize(html, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        
        // Regex para atrapar: href="/players/480249/show/lamine-yamal"
        // Grupo 1: El ID numérico (480249)
        String regex = "href=\"/players/(\\d+)/show/[^\"]*" + normalizedPlayerName + "[^\"]*\"";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(normalizedHtml);
        
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        
        throw new ScraperExtractionException("No se encontró el ID del jugador en el HTML para: " + playerName);
    }
}
