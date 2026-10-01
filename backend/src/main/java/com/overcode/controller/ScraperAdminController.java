package com.overcode.controller;

import com.overcode.service.interfaces.ExternalPlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/scraper")
@Tag(name = "Admin Scraper", description = "Endpoints de administración para gatillar manualmente el scraper (requiere X-API-KEY)")
public class ScraperAdminController {

    private final ExternalPlayerService externalPlayerService;

    public ScraperAdminController(ExternalPlayerService externalPlayerService) {
        this.externalPlayerService = externalPlayerService;
    }

    @PostMapping("/run")
    @Operation(summary = "Ejecutar scraper", description = "Dispara el scraper de jugadores de forma asíncrona. Retorna 202 inmediatamente.")
    public ResponseEntity<String> runScraper() {
        externalPlayerService.actualizarJugadoresAsync();
        return ResponseEntity.accepted().body("Scraper manual iniciado en background. Este proceso puede tardar varias horas.");
    }
}
