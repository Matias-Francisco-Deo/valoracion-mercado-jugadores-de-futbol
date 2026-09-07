package com.overcode.controller;

import com.overcode.controller.dto.PlayerDto;
import com.overcode.persistence.dto.PlayerJPADTO;
import com.overcode.persistence.repository.dao.PlayerDAOJPA;
import com.overcode.service.exception.EntidadNoEncontradaException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/")
public class PlayerController {

    private final PlayerDAOJPA playerDAOJPA;

    public PlayerController(PlayerDAOJPA playerDAOJPA) {
        this.playerDAOJPA = playerDAOJPA;
    }

    @GetMapping("/players")
    public List<PlayerDto> listPlayers() {
        return playerDAOJPA.findAll().stream()
            .map(player -> new PlayerDto(player.getId(), player.getName(), player.getCurrentPrice(), player.getTotalTokensIssued()))
            .toList();
    }

    @GetMapping("/players/{id}")
    public PlayerDto getPlayer(@PathVariable Long id) {
        PlayerJPADTO player = playerDAOJPA.findById(id)
            .orElseThrow(() -> new EntidadNoEncontradaException("Player not found: " + id));
        return new PlayerDto(player.getId(), player.getName(), player.getCurrentPrice(), player.getTotalTokensIssued());
    }
}
