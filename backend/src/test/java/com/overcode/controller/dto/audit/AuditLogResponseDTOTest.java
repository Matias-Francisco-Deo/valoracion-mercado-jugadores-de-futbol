package com.overcode.controller.dto.audit;

import com.overcode.model.AuditLog;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AuditLogResponseDTOTest {

    @Test
    void desdeModeloMapeaTodosLosCampos() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        AuditLog auditLog = new AuditLog(id, AuditLog.OperationType.BUY,
                735L, 1L, 1247L, 5, BigDecimal.valueOf(1.0), now);

        AuditLogResponseDTO dto = AuditLogResponseDTO.desdeModelo(auditLog);

        assertEquals(id, dto.id());
        assertEquals("BUY", dto.operationType());
        assertEquals(735L, dto.userId());
        assertEquals(1L, dto.counterpartyId());
        assertEquals(1247L, dto.playerId());
        assertEquals(5, dto.tokenAmount());
        assertEquals(0, BigDecimal.valueOf(1.0).compareTo(dto.pricePerToken()));
        assertEquals(now, dto.timestamp());
    }

    @Test
    void desdeModeloConNullDevuelveNull() {
        assertNull(AuditLogResponseDTO.desdeModelo(null));
    }
}
