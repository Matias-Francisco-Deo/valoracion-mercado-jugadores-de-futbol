package com.overcode.persistence.repository.dao.external.scrapper.whoscored;

import com.overcode.persistence.repository.dao.external.scrapper.exception.ScraperExtractionException;
import com.overcode.persistence.repository.dao.external.scrapper.http.ScraperHttpClient;
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

    public Long resolvePlayerId(String teamName, String playerName) {
        // Paso 1: Buscar jugador directamente
        String playerSearchUrl = WHOSCORED_URL + "/Search/?t=" + URLEncoder.encode(playerName, StandardCharsets.UTF_8);
        String searchHtml = httpClient.getHtml(playerSearchUrl);
        
        try {
            return extractPlayerIdFromHtml(searchHtml, playerName);
        } catch (ScraperExtractionException e) {
            // Fallback: Si no lo encuentra (ej. "Andriy Lunin" vs "Andrii Lunin"),
            // buscar solo por su apellido (la última palabra).
            String[] parts = playerName.split(" ");
            String lastName = parts[parts.length - 1];
            String fallbackUrl = WHOSCORED_URL + "/Search/?t=" + URLEncoder.encode(lastName, StandardCharsets.UTF_8);
            String fallbackHtml = httpClient.getHtml(fallbackUrl);
            return extractPlayerIdFromHtml(fallbackHtml, playerName);
        }
    }



    private Long extractPlayerIdFromHtml(String html, String playerName) {
        String normalizedHtml = java.text.Normalizer.normalize(html, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase();

        String[] originalWords = java.text.Normalizer.normalize(playerName, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase().split(" ");

        Pattern pattern = Pattern.compile("href=\"/players/(\\d+)/show/([^\"]+)\"");
        Matcher matcher = pattern.matcher(normalizedHtml);

        Long bestId = null;
        int maxScore = -1;

        while (matcher.find()) {
            Long currentId = Long.parseLong(matcher.group(1));
            String slug = matcher.group(2); // ej: "eder-militao"

            int score = 0;
            for (String word : originalWords) {
                // Filtramos palabras muy cortas
                if (word.length() > 2 && slug.contains(word)) {
                    score++;
                }
            }

            if (score > maxScore) {
                maxScore = score;
                bestId = currentId;
            }
        }

        if (bestId != null && maxScore > 0) {
            return bestId;
        }

        throw new ScraperExtractionException("No se encontró el ID del jugador en el HTML para: " + playerName);
    }
}
