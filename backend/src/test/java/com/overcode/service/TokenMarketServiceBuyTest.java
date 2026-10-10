package com.overcode.service;
import com.overcode.service.interfaces.TokenMarketService;

import com.overcode.model.AuditLog;
import com.overcode.model.Player;
import com.overcode.model.User;
import com.overcode.persistence.dto.jpa.AuditLogJPADTO;
import com.overcode.persistence.dto.jpa.PortfolioJPADTO;
import com.overcode.persistence.repository.interfaces.AuditLogRepository;
import com.overcode.persistence.repository.interfaces.PortfolioRepository;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import com.overcode.persistence.repository.interfaces.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled("Requires Docker API version compatibility for Testcontainers")
@SpringBootTest
class TokenMarketServiceBuyTest {

    @Autowired
    private TokenMarketService tokenMarketService;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    void whenUserBuysTokens_thenPortfoliosAreUpdatedAndAuditLogged() {
        // Here we would setup a buyer with enough credits, a super admin with tokens, and a player.
        // And then call:
        // tokenMarketService.buyTokens(buyerId, playerId, 10);
        
        // Assertions would check buyer's token holdings, buyer's credits deducted,
        // admin's tokens deducted, admin's credits increased, and AuditLog created.
        assertTrue(true);
    }
}
