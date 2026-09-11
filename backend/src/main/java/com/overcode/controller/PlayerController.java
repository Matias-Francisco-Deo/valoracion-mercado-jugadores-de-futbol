package com.overcode.controller;

import com.overcode.controller.dto.player.PlayerResponseDTO;
import com.overcode.model.Player;
import com.overcode.service.interfaces.PlayerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/")
@Tag(name = "Player", description = "Endpoints for retrieving football players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/players")
    public List<PlayerResponseDTO> listPlayers() {
        return playerService.recuperarTodos().stream()
            .map(PlayerResponseDTO::desdeModelo)
            .toList();
    }

    @GetMapping("/players/{id}")
    public ResponseEntity<PlayerResponseDTO> getPlayer(@PathVariable Long id) {
        Player player = playerService.recuperar(id);
        return ResponseEntity.ok().body(PlayerResponseDTO.desdeModelo(player));
    }
}
