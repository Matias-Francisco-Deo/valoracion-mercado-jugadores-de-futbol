package com.overcode.controller.dto.player;

import org.springframework.data.domain.Page;

import java.util.List;

public record PlayerPageResponseDTO(
        List<PlayerResponseDTO> content,
        int number,//pagina actual
        int size,//jugadores por pagina
        int totalPages,
        long totalElements//total de jugadores
) {

    public static PlayerPageResponseDTO desdeModelo(Page<PlayerResponseDTO> page) {
        return new PlayerPageResponseDTO(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}
