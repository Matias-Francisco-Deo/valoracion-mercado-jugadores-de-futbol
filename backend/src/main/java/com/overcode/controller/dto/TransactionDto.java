package com.overcode.controller.dto;

import java.time.Instant;

public record TransactionDto(Long id, Instant timestamp, String type, Long buyerId, Long sellerId,
                           Long playerId, Integer quantity, Integer unitPrice, Integer totalAmount) {
}
