package com.overcode.controller;

import com.overcode.controller.dto.cotizacion.ActualizarFactorEscalaRequestDTO;
import com.overcode.controller.dto.cotizacion.EstrategiaCotizacionResponseDTO;
import com.overcode.service.interfaces.EstrategiaCotizacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/quotes")
@Tag(name = "Admin Estrategias de Cotización", description = "Endpoints de administración para gestionar estrategias de cotización de jugadores (requiere X-API-KEY)")
public class EstrategiaCotizacionController {

    private final EstrategiaCotizacionService estrategiaCotizacionService;

    public EstrategiaCotizacionController(EstrategiaCotizacionService estrategiaCotizacionService) {
        this.estrategiaCotizacionService = estrategiaCotizacionService;
    }

    @GetMapping
    @Operation(summary = "Listar estrategias", description = "Retorna todas las estrategias de cotización registradas en el sistema.")
    public ResponseEntity<List<EstrategiaCotizacionResponseDTO>> recuperarTodos() {
        List<EstrategiaCotizacionResponseDTO> estrategias = estrategiaCotizacionService.recuperarTodos()
                .stream()
                .map(EstrategiaCotizacionResponseDTO::desdeModelo)
                .toList();
        return ResponseEntity.ok(estrategias);
    }

    @PostMapping("/{id}/select")
    @Operation(summary = "Seleccionar estrategia activa", description = "Designa la estrategia identificada por {id} como la estrategia activa para cotizaciones.")
    public ResponseEntity<Void> seleccionarEstrategia(@PathVariable Long id) {
        estrategiaCotizacionService.seleccionarEstrategia(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar factor de escala", description = "Actualiza el factor de escala de una estrategia existente. El factor debe ser estrictamente mayor a 0.")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarFactorEscalaRequestDTO requestDTO) {
        estrategiaCotizacionService.actualizar(id, requestDTO.factorEscala());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/recalculate")
    @Operation(summary = "Cotizar jugadores", description = "Dispara la recotización masiva de jugadores usando la estrategia activa. Retorna confirmación de finalización.")
    public ResponseEntity<String> cotizarJugadores() {
        estrategiaCotizacionService.cotizarJugadores();
        return ResponseEntity.ok("Cotización de jugadores finalizada exitosamente");
    }
}
