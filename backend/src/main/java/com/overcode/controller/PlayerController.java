package com.overcode.controller;

import com.overcode.controller.dto.cotizacion.RegistroCotizacionResponseDTO;
import com.overcode.controller.dto.player.PlayerFilter;
import com.overcode.controller.dto.player.PlayerFilterDTO;
import com.overcode.controller.dto.player.PlayerPageResponseDTO;
import com.overcode.controller.dto.player.PlayerResponseDTO;
import com.overcode.model.Player;
import com.overcode.model.cotizacion.registro.BusquedaRegistroCotizacion;
import com.overcode.model.cotizacion.registro.RegistroCotizacion;
import com.overcode.service.interfaces.PlayerService;
import com.overcode.service.interfaces.RegistroCotizacionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/players")
@Tag(name = "Player", description = "Endpoints for retrieving football players")
public class PlayerController {

    private final PlayerService playerService;
    private final RegistroCotizacionService registroCotizacionService;

    public PlayerController(PlayerService playerService, RegistroCotizacionService registroCotizacionService) {
        this.playerService = playerService;
        this.registroCotizacionService = registroCotizacionService;
    }

    @GetMapping({"", "/"})
    public PlayerPageResponseDTO listPlayers(
            @RequestParam(required = false) String league,
            @RequestParam(required = false) String clubName,
            Pageable pageable
    ) {
        PlayerFilterDTO playerFilterDTO = new PlayerFilterDTO(clubName, league);

        PlayerFilter filter = playerFilterDTO.aModelo();

        Page<PlayerResponseDTO> page = playerService
                .recuperarTodosConFiltro(filter, pageable)
                .map(PlayerResponseDTO::desdeModelo);

        return PlayerPageResponseDTO.desdeModelo(page);
    }

    @GetMapping("/top")
    public List<PlayerResponseDTO> listTopPlayers() {
        return playerService.listarTop5JugadoresPorRating().stream()
                .map(PlayerResponseDTO::desdeModelo)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponseDTO> getPlayer(@PathVariable Long id) {
        Player player = playerService.recuperar(id);
        return ResponseEntity.ok().body(PlayerResponseDTO.desdeModelo(player));
    }

    @GetMapping("{id}/quotes")
    public ResponseEntity<List<RegistroCotizacionResponseDTO>> getPlayerQuotes(
            @PathVariable Long id,
            @RequestParam() LocalDateTime startDate,
            @RequestParam() LocalDateTime endDate
    ) {
        BusquedaRegistroCotizacion busqueda = new BusquedaRegistroCotizacion(id, startDate, endDate);
        List<RegistroCotizacion> registros = registroCotizacionService.recuperarRegistrosDeJugador(busqueda);
        return ResponseEntity.ok().body(
                registros.stream().map(RegistroCotizacionResponseDTO::desdeModelo)
                        .toList()
        );
    }
}