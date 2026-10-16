package com.overcode.controller.dto.token;

import com.overcode.model.dto.SummaryTokenPlayerDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public record TokenPageResponseDTO(
        List<SummaryTokenPlayerDTO> content,
        int number,
        int size,
        int totalPages,
        long totalElements
) {
    public static TokenPageResponseDTO desdeModelo(Page<SummaryTokenPlayerDTO> page) {
        return new TokenPageResponseDTO(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}
