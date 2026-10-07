package com.overcode.persistence.repository.dao.external.scrapper.util;

import com.overcode.persistence.repository.dao.external.scrapper.exception.ScraperExtractionException;

import java.util.Locale;

public class JsonExtractorUtil {

    private JsonExtractorUtil() {
        // Utility class
    }

    /**
     * Extrae el bloque JSON asignado a require.config.params['args'] del HTML sin procesar.
     * Utiliza búsqueda de cadenas (O(N)) para evitar problemas de super-linear backtracking
     * en expresiones regulares evaluadas por SonarCloud o motores de análisis estático.
     *
     * @param html La cadena HTML sin procesar de WhoScored.
     * @return La cadena JSON pura.
     * @throws ScraperExtractionException si no se encuentra el bloque.
     */
    public static String extractPlayerStatsJson(String html) {
        if (html == null || html.isBlank()) {
            throw new ScraperExtractionException("HTML content is null or empty.");
        }

        int keyIndex = html.indexOf("require.config.params['args']");
        if (keyIndex == -1) {
            keyIndex = html.indexOf("require.config.params[\"args\"]");
        }

        if (keyIndex == -1) {
            throw new ScraperExtractionException("Could not find require.config.params['args'] in HTML.");
        }

        int startJson = html.indexOf("{", keyIndex);
        if (startJson == -1) {
            throw new ScraperExtractionException("Could not find JSON opening brace.");
        }

        String candidate = html.substring(startJson);
        int endScript = candidate.indexOf("</script>");
        if (endScript == -1) {
            endScript = candidate.toLowerCase(Locale.ROOT).indexOf("</script>");
        }

        if (endScript == -1) {
            throw new ScraperExtractionException("Could not find closing script tag.");
        }

        String scriptContent = candidate.substring(0, endScript);
        int lastBrace = scriptContent.lastIndexOf("}");
        if (lastBrace == -1) {
            throw new ScraperExtractionException("Could not find JSON closing brace.");
        }

        return scriptContent.substring(0, lastBrace + 1).trim();
    }

    /**
     * Extrae la posicion del jugador del HTML.
     */
    public static String extractPlayerPosition(String html) {
        if (html == null || html.isBlank()) {
            return null;
        }

        String normalizedHtml = html.toLowerCase(Locale.ROOT);

        int metaIndex = normalizedHtml.indexOf("<meta name=\"description\"");
        if (metaIndex != -1) {
            String metaTag = html.substring(metaIndex, html.indexOf('>', metaIndex));
            String position = normalizePosition(metaTag);
            if (position != null) {
                return position;
            }

            int contentIndex = metaTag.toLowerCase(Locale.ROOT).indexOf("content=");
            if (contentIndex != -1) {
                String contentValue = metaTag.substring(contentIndex + 8).trim();
                contentValue = stripQuotes(contentValue);
                String normalized = normalizePosition(contentValue);
                if (normalized != null) {
                    return normalized;
                }
            }
        }

        String[] markers = {"positions:", "position:"};
        for (String marker : markers) {
            int start = normalizedHtml.indexOf(marker);
            if (start == -1) {
                continue;
            }

            int valueStart = start + marker.length();
            int valueEnd = html.indexOf("</span>", valueStart);
            if (valueEnd == -1) {
                valueEnd = html.indexOf("<", valueStart);
            }
            if (valueEnd == -1) {
                return null;
            }

            String candidate = html.substring(valueStart, valueEnd).trim();
            String position = normalizePosition(candidate);
            if (position != null) {
                return position;
            }
        }

        return null;
    }

    private static String normalizePosition(String candidate) {
        if (candidate == null) {
            return null;
        }

        String value = candidate.toLowerCase(Locale.ROOT);
        if (value.contains("goalkeeper")) {
            return "Goalkeeper";
        }
        if (value.contains("defender")) {
            return "Defender";
        }
        if (value.contains("midfielder")) {
            return "Midfielder";
        }
        if (value.contains("forward") || value.contains("attacker")) {
            return "Forward";
        }
        return null;
    }

    public static String stripQuotes(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        String trimmed = value.trim();
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
            return trimmed.substring(1, trimmed.length() - 1);
        }
        if (trimmed.startsWith("'") && trimmed.endsWith("'")) {
            return trimmed.substring(1, trimmed.length() - 1);
        }
        return trimmed;
    }
}
