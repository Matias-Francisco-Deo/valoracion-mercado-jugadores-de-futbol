package com.overcode.controller.dto;

import java.time.Instant;

public record TradeResponse(Long transactionId, Long buyerId, Long sellerId, Long playerId,
                           Integer quantity, Integer unitPrice, Integer totalAmount, Instant timestamp) {
}
