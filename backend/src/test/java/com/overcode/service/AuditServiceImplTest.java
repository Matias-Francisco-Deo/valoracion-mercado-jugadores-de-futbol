package com.overcode.service;

import com.overcode.model.AuditLog;
import com.overcode.persistence.repository.interfaces.AuditLogRepository;
import com.overcode.service.impl.AuditServiceImpl;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class AuditServiceImplTest {

    @Test
    void listarTodoDevuelveLosRegistrosDelRepositorio() {
        AuditLogRepository repository = mock(AuditLogRepository.class);
        AuditLog emission = new AuditLog(UUID.randomUUID(), AuditLog.OperationType.EMISSION,
                1L, 1L, 1247L, 100, BigDecimal.valueOf(1.0), Instant.now());
        AuditLog buy = new AuditLog(UUID.randomUUID(), AuditLog.OperationType.BUY,
                735L, 1L, 1247L, 5, BigDecimal.valueOf(1.0), Instant.now());
        when(repository.findAll()).thenReturn(List.of(emission, buy));

        AuditServiceImpl service = new AuditServiceImpl(repository);
        List<AuditLog> result = service.listarTodo();

        assertEquals(2, result.size());
        assertSame(emission, result.get(0));
        assertSame(buy, result.get(1));
        verify(repository, times(1)).findAll();
    }

    @Test
    void listarTodoDevuelveListaVaciaCuandoNoHayRegistros() {
        AuditLogRepository repository = mock(AuditLogRepository.class);
        when(repository.findAll()).thenReturn(List.of());

        AuditServiceImpl service = new AuditServiceImpl(repository);

        assertEquals(0, service.listarTodo().size());
    }
}
