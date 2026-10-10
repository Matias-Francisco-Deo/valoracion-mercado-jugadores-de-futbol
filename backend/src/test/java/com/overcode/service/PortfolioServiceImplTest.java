package com.overcode.service;

import com.overcode.model.Portfolio;
import com.overcode.persistence.repository.interfaces.PortfolioRepository;
import com.overcode.service.impl.PortfolioServiceImpl;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PortfolioServiceImplTest {

    private PortfolioRepository repositoryReturningSavedArg() {
        PortfolioRepository repository = mock(PortfolioRepository.class);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        return repository;
    }

    @Test
    void createForUserCuandoNoExisteCreaPortfolioVacioConVersionNull() {
        PortfolioRepository repository = repositoryReturningSavedArg();
        when(repository.findByUserId(900L)).thenReturn(Optional.empty());

        PortfolioServiceImpl service = new PortfolioServiceImpl(repository);
        Portfolio result = service.createForUser(900L);

        assertEquals(900L, result.getUserId());
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getCredits()));
        // null version so Spring Data issues an INSERT instead of a merge
        assertNull(result.getVersion());
        verify(repository, times(1)).save(any());
    }

    @Test
    void createForUserCuandoYaExisteDevuelveElExistenteYNoGuarda() {
        PortfolioRepository repository = mock(PortfolioRepository.class);
        Portfolio existing = new Portfolio(UUID.randomUUID(), 735L, BigDecimal.valueOf(100), 0L);
        when(repository.findByUserId(735L)).thenReturn(Optional.of(existing));

        PortfolioServiceImpl service = new PortfolioServiceImpl(repository);
        Portfolio result = service.createForUser(735L);

        assertSame(existing, result);
        verify(repository, never()).save(any());
    }

    @Test
    void depositSumaCreditosAlPortfolioExistente() {
        PortfolioRepository repository = repositoryReturningSavedArg();
        Portfolio existing = new Portfolio(UUID.randomUUID(), 735L, BigDecimal.valueOf(100), 0L);
        when(repository.findByUserId(735L)).thenReturn(Optional.of(existing));

        PortfolioServiceImpl service = new PortfolioServiceImpl(repository);
        Portfolio result = service.deposit(735L, BigDecimal.valueOf(50));

        assertEquals(0, BigDecimal.valueOf(150).compareTo(result.getCredits()));
        verify(repository, times(1)).save(any());
    }

    @Test
    void depositCreaElPortfolioSiNoExiste() {
        PortfolioRepository repository = repositoryReturningSavedArg();
        when(repository.findByUserId(900L)).thenReturn(Optional.empty());

        PortfolioServiceImpl service = new PortfolioServiceImpl(repository);
        Portfolio result = service.deposit(900L, BigDecimal.valueOf(500));

        assertEquals(900L, result.getUserId());
        assertEquals(0, BigDecimal.valueOf(500).compareTo(result.getCredits()));
    }

    @Test
    void depositConMontoNoPositivoLanzaIllegalArgument() {
        PortfolioRepository repository = mock(PortfolioRepository.class);
        Portfolio existing = new Portfolio(UUID.randomUUID(), 735L, BigDecimal.valueOf(100), 0L);
        when(repository.findByUserId(735L)).thenReturn(Optional.of(existing));

        PortfolioServiceImpl service = new PortfolioServiceImpl(repository);

        assertThrows(IllegalArgumentException.class,
                () -> service.deposit(735L, BigDecimal.valueOf(-5)));
        verify(repository, never()).save(any());
    }

    @Test
    void getByUserIdCuandoNoExisteLanzaIllegalState() {
        PortfolioRepository repository = mock(PortfolioRepository.class);
        when(repository.findByUserId(5L)).thenReturn(Optional.empty());

        PortfolioServiceImpl service = new PortfolioServiceImpl(repository);

        assertThrows(IllegalStateException.class, () -> service.getByUserId(5L));
    }
}
