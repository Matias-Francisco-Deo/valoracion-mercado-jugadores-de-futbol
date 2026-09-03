package com.overcode.controller;

import com.overcode.controller.dto.PlayerDto;
import com.overcode.persistency.dto.PlayerRecord;
import com.overcode.persistency.repository.PlayerRepository;
import com.overcode.service.exception.NotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/")
public class PlayerController {

    private final PlayerRepository playerRepository;

    public PlayerController(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @GetMapping("/players")
    public List<PlayerDto> listPlayers() {
        return playerRepository.findAll().stream()
            .map(player -> new PlayerDto(player.getId(), player.getName(), player.getCurrentPrice(), player.getTotalIssued()))
            .toList();
    }

    @GetMapping("/players/{id}")
    public PlayerDto getPlayer(@PathVariable Long id) {
        PlayerRecord player = playerRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Player not found: " + id));
        return new PlayerDto(player.getId(), player.getName(), player.getCurrentPrice(), player.getTotalIssued());
    }
}
