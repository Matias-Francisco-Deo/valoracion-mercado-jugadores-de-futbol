package com.overcode.controller.dto.transaction;

import com.overcode.model.transaction.Transaction;
import org.springframework.data.domain.Page;

import java.util.List;

public record TransactionPageResponseDTO(
        List<TransactionResponseDTO> content,
        int number,
        int size,
        int totalPages,
        long totalElements
) {
    public static TransactionPageResponseDTO desdeModelo(Page<Transaction> page) {
        return new TransactionPageResponseDTO(
                page.getContent().stream().map(TransactionResponseDTO::desdeModelo).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}