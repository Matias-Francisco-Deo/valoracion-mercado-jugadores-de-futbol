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
}
