package com.overcode.persistence.repository.dao.external.scrapper.util;

import com.overcode.persistence.repository.dao.external.scrapper.exception.ScraperExtractionException;

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
        if (html == null || html.isEmpty()) {
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

        int endScript = html.indexOf("</script>", startJson);
        if (endScript == -1) {
            // WhoScored HTML is heavily standardized, but just in case:
            endScript = html.toLowerCase().indexOf("</script>", startJson);
        }

        if (endScript == -1) {
            throw new ScraperExtractionException("Could not find closing script tag.");
        }

        String scriptContent = html.substring(startJson, endScript);

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
        if (html == null || html.isEmpty()) {
            return null;
        }
        
        try {
            java.nio.file.Files.writeString(java.nio.file.Paths.get("debug_html.txt"), html);
        } catch (Exception e) {}

        // Primero intentamos buscar en la meta description que es mas estandar
        int descIndex = html.indexOf("<meta name=\"description\"");
        if (descIndex != -1) {
            int endDesc = html.indexOf(">", descIndex);
            if (endDesc != -1) {
                String desc = html.substring(descIndex, endDesc).toLowerCase();
                if (desc.contains("defender")) return "Defender";
                if (desc.contains("goalkeeper")) return "Goalkeeper";
                if (desc.contains("midfielder")) return "Midfielder";
                if (desc.contains("forward") || desc.contains("attacker")) return "Forward";
            }
        }

        // Fallback al HTML de la vista
        String searchKey = "Positions:";
        int keyIndex = html.indexOf(searchKey);
        if (keyIndex == -1) {
            searchKey = "Position:";
            keyIndex = html.indexOf(searchKey);
            if (keyIndex == -1) {
                return null;
            }
        }

        String spanStyle = "inline-block";
        int startSpan = html.indexOf(spanStyle, keyIndex);
        if (startSpan == -1) {
            return null;
        }
        
        startSpan = html.indexOf(">", startSpan);
        if (startSpan == -1) {
            return null;
        }
        startSpan += 1;

        int endSpan = html.indexOf("</span>", startSpan);
        if (endSpan == -1) {
            return null;
        }

        String fullPosition = html.substring(startSpan, endSpan).trim();
        int parenthesisIndex = fullPosition.indexOf("(");
        if (parenthesisIndex != -1) {
            return fullPosition.substring(0, parenthesisIndex).trim();
        }
        return fullPosition;
    }
}
