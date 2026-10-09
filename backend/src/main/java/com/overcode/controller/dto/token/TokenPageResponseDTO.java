package com.overcode.controller.dto.token;

import com.overcode.controller.dto.player.PlayerPageResponseDTO;
import com.overcode.controller.dto.player.PlayerResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public record TokenPageResponseDTO(
        List<TokenSearchResponseDTO> content,
        int number,
        int size,
        int totalPages,
        long totalElements
) {
    public static TokenPageResponseDTO desdeModelo(Page<TokenSearchResponseDTO> page) {
        return new TokenPageResponseDTO(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}
