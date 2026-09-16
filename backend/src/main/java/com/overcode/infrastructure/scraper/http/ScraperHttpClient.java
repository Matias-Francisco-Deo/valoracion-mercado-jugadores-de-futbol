package com.overcode.infrastructure.scraper.http;

import com.microsoft.playwright.*;
import com.overcode.infrastructure.scraper.exception.ScraperExtractionException;
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
        try (Playwright playwright = Playwright.create()) {
            
            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                    .setHeadless(true);
            
            try (Browser browser = playwright.chromium().launch(launchOptions)) {
                
                Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                        .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36");
                
                BrowserContext context = browser.newContext(contextOptions);
                Page page = context.newPage();
                
                page.navigate(url);
                
                // CRÍTICO: Espera de 3.5s requerida para resolver el desafío matemático de Cloudflare
                page.waitForTimeout(3500); 
                
                String content = page.content();
                if (content.contains("Cloudflare") && content.contains("Checking your browser")) {
                    throw new ScraperExtractionException("Cloudflare challenge no superado en 3.5s para la URL: " + url);
                }
                
                return content;
            }
        } catch (Exception e) {
            throw new ScraperExtractionException("Error fatal ejecutando Playwright para la URL: " + url, e);
        }
    }
}
