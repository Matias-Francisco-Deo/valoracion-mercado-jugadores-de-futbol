package com.overcode.infrastructure.scraper.util;

import com.overcode.infrastructure.scraper.exception.ScraperExtractionException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonExtractorUtil {

    // Coincide con la asignación específica de JavaScript y extrae el objeto JSON.
    // Pattern.DOTALL permite que el '.' coincida con los caracteres de salto de línea.
    private static final Pattern ARGS_JSON_PATTERN = Pattern.compile(
            "require\\.config\\.params\\['args'\\]\\s*=\\s*(\\{.*?\\});\\s*</script>",
            Pattern.DOTALL
    );

    private JsonExtractorUtil() {
        // Utility class
    }

    /**
     * Extrae el bloque JSON asignado a require.config.params['args'] del HTML sin procesar.
     *
     * @param html La cadena HTML sin procesar de WhoScored.
     * @return La cadena JSON pura.
     * @throws ScraperExtractionException si no se encuentra el bloque.
     */
    public static String extractPlayerStatsJson(String html) {
        if (html == null || html.isEmpty()) {
            throw new ScraperExtractionException("HTML content is null or empty.");
        }

        Matcher matcher = ARGS_JSON_PATTERN.matcher(html);
        if (matcher.find()) {
            // Group 1 contains the actual JSON object block
            return matcher.group(1).trim();
        }

        throw new ScraperExtractionException("Could not find the JSON block for player stats in the HTML.");
    }
}
