package com.overcode.domain.service;

import com.overcode.infrastructure.scraper.exception.ScraperExtractionException;
import com.overcode.infrastructure.scraper.http.ScraperHttpClient;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PlayerMappingService {

    private final ScraperHttpClient httpClient;

    public PlayerMappingService(ScraperHttpClient httpClient) {
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
        String teamSearchUrl = "https://www.whoscored.com/Search/?t=" + URLEncoder.encode(teamName, StandardCharsets.UTF_8);
        String searchHtml = httpClient.getHtml(teamSearchUrl);
        
        Long teamId = extractTeamIdFromHtml(searchHtml, teamName);
        
        // Paso 2: Con el Team ID, ir a la página del equipo para buscar al jugador
        // Nota: WhoScored redirige o formatea la URL, pero el ID es suficiente para la ruta base.
        // Ej: https://www.whoscored.com/Teams/65
        String teamUrl = "https://www.whoscored.com/Teams/" + teamId;
        String teamHtml = httpClient.getHtml(teamUrl);
        
        // Paso 3: Buscar el ID del jugador aislando el href con Regex (sin usar DOM)
        return extractPlayerIdFromHtml(teamHtml, playerName);
    }

    private Long extractTeamIdFromHtml(String html, String teamName) {
        // Formatear el nombre para la URL (ej: "Barcelona" -> "barcelona")
        String formattedName = teamName.toLowerCase().replace(" ", "-");
        
        // Regex para atrapar: href="/teams/65/show/spain-barcelona"
        // Grupo 1: El ID numérico (65)
        String regex = "href=\"/teams/(\\d+)/show/[^\"]*" + formattedName + "[^\"]*\"";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);
        
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        
        throw new ScraperExtractionException("No se encontró el ID del equipo en el HTML para: " + teamName);
    }

    private Long extractPlayerIdFromHtml(String html, String playerName) {
        // Formatear el nombre para la URL (ej: "Lamine Yamal" -> "lamine-yamal")
        String formattedName = playerName.toLowerCase().replace(" ", "-");
        
        // Regex para atrapar: href="/players/480249/show/lamine-yamal"
        // Grupo 1: El ID numérico (480249)
        String regex = "href=\"/players/(\\d+)/show/[^\"]*" + formattedName + "[^\"]*\"";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);
        
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        
        throw new ScraperExtractionException("No se encontró el ID del jugador en el HTML para: " + playerName);
    }
}
