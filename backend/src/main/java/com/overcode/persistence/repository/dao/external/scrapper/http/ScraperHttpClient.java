package com.overcode.persistence.repository.dao.external.scrapper.http;

import com.microsoft.playwright.*;
import com.overcode.persistence.repository.dao.external.scrapper.exception.ScraperExtractionException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class ScraperHttpClient {

    private Playwright playwright;
    private Browser browser;

    @PostConstruct
    public void init() {
        this.playwright = Playwright.create();
        
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(true);

        this.browser = playwright.chromium().launch(launchOptions);
    }

    @PreDestroy
    public void cleanup() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    /**
     * Utiliza Playwright para ejecutar el JavaScript de WhoScored y pasar la validación Anti-Bot.
     * Sigue estando sincronizado para evitar consumir toda la memoria si llegan muchos requests juntos.
     * Para procesar 2500 jugadores rápido, podrías sacar el synchronized y usar un pool de contexts, 
     * pero con el browser singleton ya bajaste el tiempo radicalmente.
     */
    public synchronized String getHtml(String url) {
        int maxRetries = 2;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                String content = getHtmlPlaywright(url, attempt, maxRetries);
                if (content != null) return content;
            } catch (Exception e) {
                if (attempt == maxRetries) {
                    throw new ScraperExtractionException("Error fatal ejecutando Playwright para la URL: " + url, e);
                }
            }
        }
        throw new ScraperExtractionException("Fallo inesperado obteniendo HTML para: " + url);
    }

    private String getHtmlPlaywright(String url, int attempt, int maxRetries) {
        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36");

        try (BrowserContext context = browser.newContext(contextOptions);
             Page page = context.newPage()) {

            // Interceptamos y abortamos las peticiones de basura gráfica para acelerar la carga por 10
            page.route("**/*", route -> {
                String type = route.request().resourceType();
                String requestUrl = route.request().url().toLowerCase();
                
                if ("image".equals(type) || "stylesheet".equals(type) || "font".equals(type) || "media".equals(type) ||
                    requestUrl.contains("google-analytics") || requestUrl.contains("doubleclick") || 
                    requestUrl.contains("ads") || requestUrl.contains("tracker") || requestUrl.contains("pixel")) {
                    route.abort();
                } else {
                    route.resume();
                }
            });

            page.navigate(url);

            // Ahora esperamos solo a que se arme el HTML, no nos importa que terminen de cargar cosas de red.
            try {
                page.waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED, 
                    new Page.WaitForLoadStateOptions().setTimeout(5000));
            } catch (TimeoutError e) {
                // Timeout ignorado
            }

            String content = page.content();
            
            if (content.contains("Cloudflare") && content.contains("Checking your browser")) {
                if (attempt == maxRetries) {
                    throw new ScraperExtractionException("Cloudflare challenge no superado para la URL: " + url);
                }
                return null;
            }

            return content;
        }
    }
}
