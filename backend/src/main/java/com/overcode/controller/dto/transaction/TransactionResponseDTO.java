package com.overcode.controller.dto.transaction;

import com.overcode.model.transaction.Transaction;
import com.overcode.model.transaction.TransactionType;

import java.time.LocalDateTime;

public record TransactionResponseDTO(
        Long id,
        String playerName,
        LocalDateTime date,
        Integer quantity,
        Double unitPrice,
        Double totalPrice,
        TransactionType type
) {
    public static TransactionResponseDTO desdeModelo(Transaction transaction) {
        return new TransactionResponseDTO(
                transaction.getId(),
                transaction.getPlayer().getName(),
                transaction.getDate(),
                transaction.getQuantity(),
                transaction.getUnitPrice(),
                transaction.getTotalPrice(),
                transaction.getType()
        );
    }
}