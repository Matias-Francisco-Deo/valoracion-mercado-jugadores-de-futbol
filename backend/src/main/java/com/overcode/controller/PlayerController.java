package com.overcode.controller;

import com.overcode.controller.dto.player.PlayerFilter;
import com.overcode.controller.dto.player.PlayerFilterDTO;
import com.overcode.controller.dto.player.PlayerResponseDTO;
import com.overcode.model.Player;
import com.overcode.service.interfaces.PlayerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/players")
@Tag(name = "Player", description = "Endpoints for retrieving football players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping({"", "/"})
    public List<PlayerResponseDTO> listPlayers(
            @RequestParam(required = false) String league,
            @RequestParam(required = false) String clubName
    ) {
        PlayerFilterDTO playerFilterDTO = new PlayerFilterDTO(league, clubName);

        PlayerFilter filter = playerFilterDTO.aModelo();
        return playerService.recuperarTodosConFiltro(filter).stream()
                .map(PlayerResponseDTO::desdeModelo)
                .toList();
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
}