package com.overcode.controller;

import com.overcode.service.interfaces.ExternalPlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/players")
@Tag(name = "Admin Players", description = "Endpoints de administración para gatillar manualmente la actualización de jugadores (requiere X-API-KEY)")
public class ExternalPlayerController {

    private final ExternalPlayerService externalPlayerService;

    public ExternalPlayerController(ExternalPlayerService externalPlayerService) {
        this.externalPlayerService = externalPlayerService;
    }

    @PostMapping("/update")
    @Operation(summary = "Ejecutar scrapper", description = "Dispara el scrapper de jugadores de forma asíncrona. Retorna 202 inmediatamente.")
    public ResponseEntity<String> runScrapper() {
        externalPlayerService.actualizarJugadoresAsync();
        return ResponseEntity.accepted().body("Actualización iniciada. Este proceso puede tardar varias horas.");
    }
}
