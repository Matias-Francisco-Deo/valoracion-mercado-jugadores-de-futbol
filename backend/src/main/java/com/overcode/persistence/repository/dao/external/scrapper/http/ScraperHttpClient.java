package com.overcode.persistence.repository.dao.external.scrapper.http;

import com.microsoft.playwright.*;
import com.overcode.persistence.repository.dao.external.scrapper.exception.ScraperExtractionException;
import org.springframework.stereotype.Component;

@Component
public class ScraperHttpClient {

    /**
     * Utiliza Playwright para levantar un navegador Chromium real en modo "headless" (invisible).
     * Esto permite ejecutar el JavaScript de WhoScored y pasar la validación Anti-Bot de Cloudflare
     * antes de extraer el código HTML resultante.
     * 
     * Nota Arquitectónica: Este método está sincronizado por simplicidad (Playwright no es thread-safe).
     * En un entorno de altísima concurrencia, esto debería migrarse a un Pool de navegadores.
     * 
     * @param url La URL objetivo a scrapear.
     * @return El String del HTML renderizado final.
     */
    public synchronized String getHtml(String url) {
        int maxRetries = 2;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try (Playwright playwright = Playwright.create()) {
                String content = getHtmlPlaywright(url, playwright, attempt, maxRetries);
                if (content != null) return content;
            } catch (Exception e) {
                if (attempt == maxRetries) {
                    throw new ScraperExtractionException("Error fatal ejecutando Playwright para la URL: " + url, e);
                }
            }

        }
        throw new ScraperExtractionException("Fallo inesperado obteniendo HTML para: " + url);
    }

    private static String getHtmlPlaywright(String url, Playwright playwright, int attempt, int maxRetries) {
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(true);

        BrowserContext context = null;
        Page page = null;
        try (Browser browser = playwright.chromium().launch(launchOptions)) { // TODO mejorar trys?

            Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36");

            context = browser.newContext(contextOptions);
            page = context.newPage();

            page.navigate(url);

            // Esperamos 5 segundos para darle tiempo a Cloudflare Y a las llamadas AJAX de WhoScored
            page.waitForTimeout(5000);

            String content = page.content();
            if (content.contains("Cloudflare") && content.contains("Checking your browser")) {
                if (attempt == maxRetries) {
                    throw new ScraperExtractionException("Cloudflare challenge no superado en 5s para la URL: " + url);
                }
                return null;
            }

            return content;
        } finally {
            if (page != null) page.close();
            if (context != null) context.close();
        }

    }
}
