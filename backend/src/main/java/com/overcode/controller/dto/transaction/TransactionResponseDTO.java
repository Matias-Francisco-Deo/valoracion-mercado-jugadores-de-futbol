package com.overcode.controller.dto.transaction;

import com.overcode.model.venta.transaction.TransaccionFinanciera;
import com.overcode.model.venta.transaction.TransactionType;

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
    public static TransactionResponseDTO desdeModelo(TransaccionFinanciera transaccionFinanciera) {
        return new TransactionResponseDTO(
                transaccionFinanciera.getId(),
                transaccionFinanciera.getPlayer().getName(),
                transaccionFinanciera.getDate(),
                transaccionFinanciera.getQuantity(),
                transaccionFinanciera.getUnitPrice(),
                transaccionFinanciera.getTotalPrice(),
                transaccionFinanciera.getType()
        );
    }
}