package com.overcode.persistence;

import com.overcode.persistence.dto.jpa.PortfolioJPADTO;
import com.overcode.persistence.repository.dao.jpa.PortfolioDAOJPA;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Disabled("Requires Docker API version compatibility for Testcontainers")
@SpringBootTest
@Testcontainers
class PortfolioRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private PortfolioDAOJPA portfolioRepository;

    @Test
    void testOptimisticLocking() {
        // Setup
        PortfolioJPADTO portfolio = new PortfolioJPADTO(UUID.randomUUID(), 1L, BigDecimal.valueOf(100));
        portfolio = portfolioRepository.saveAndFlush(portfolio);

        // Transaction 1
        PortfolioJPADTO tx1 = portfolioRepository.findById(portfolio.getId()).orElseThrow();
        // Transaction 2
        PortfolioJPADTO tx2 = portfolioRepository.findById(portfolio.getId()).orElseThrow();

        // Tx1 modifies and saves
        tx1.setCredits(BigDecimal.valueOf(50));
        portfolioRepository.saveAndFlush(tx1);

        // Tx2 modifies and tries to save -> should throw exception
        tx2.setCredits(BigDecimal.valueOf(200));
        
        assertThrows(ObjectOptimisticLockingFailureException.class, () -> {
            portfolioRepository.saveAndFlush(tx2);
        });
    }
}
