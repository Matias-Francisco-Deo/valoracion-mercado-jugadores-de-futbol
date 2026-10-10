package com.overcode.controller;

import com.overcode.model.AuditLog;
import com.overcode.service.interfaces.AuditService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuditLogControllerTest {

    @Test
    void listarDevuelveOkYLosRegistrosMapeados() throws Exception {
        AuditService auditService = mock(AuditService.class);
        AuditLog buy = new AuditLog(UUID.randomUUID(), AuditLog.OperationType.BUY,
                735L, 1L, 1247L, 5, BigDecimal.valueOf(1.0), Instant.now());
        when(auditService.listarTodo()).thenReturn(List.of(buy));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new AuditLogController(auditService)).build();

        mockMvc.perform(get("/api/admin/audits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].operationType").value("BUY"))
                .andExpect(jsonPath("$[0].userId").value(735))
                .andExpect(jsonPath("$[0].counterpartyId").value(1))
                .andExpect(jsonPath("$[0].playerId").value(1247))
                .andExpect(jsonPath("$[0].tokenAmount").value(5));

        verify(auditService, times(1)).listarTodo();
    }

    @Test
    void listarDevuelveArrayVacioCuandoNoHayRegistros() throws Exception {
        AuditService auditService = mock(AuditService.class);
        when(auditService.listarTodo()).thenReturn(List.of());

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new AuditLogController(auditService)).build();

        mockMvc.perform(get("/api/admin/audits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
