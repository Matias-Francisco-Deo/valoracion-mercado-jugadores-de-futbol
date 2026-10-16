package com.overcode.controller.dto.token;

import com.overcode.model.dto.SummaryTokenPlayerDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public record SummaryTokenPageResponseDTO(
        List<SummaryTokenPlayerResponseDTO> content,
        int number,
        int size,
        int totalPages,
        long totalElements
) {
    public static SummaryTokenPageResponseDTO desdeModelo(Page<SummaryTokenPlayerDTO> page) {
        return new SummaryTokenPageResponseDTO(
                page.getContent().stream().map(SummaryTokenPlayerResponseDTO::desdeModelo).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}
