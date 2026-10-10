package com.overcode.controller.dto.audit;

import com.overcode.model.AuditLog;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AuditLogResponseDTO(
        UUID id,
        String operationType,
        Long userId,
        Long counterpartyId,
        Long playerId,
        int tokenAmount,
        BigDecimal pricePerToken,
        Instant timestamp
) {
    public static AuditLogResponseDTO desdeModelo(AuditLog auditLog) {
        if (auditLog == null) {
            return null;
        }
        return new AuditLogResponseDTO(
                auditLog.getId(),
                auditLog.getOperationType().name(),
                auditLog.getUserId(),
                auditLog.getCounterpartyId(),
                auditLog.getPlayerId(),
                auditLog.getTokenAmount(),
                auditLog.getPricePerToken(),
                auditLog.getTimestamp()
        );
    }
}
