package com.overcode.service.impl;

import com.overcode.model.AuditLog;
import com.overcode.model.Player;
import com.overcode.model.Portfolio;
import com.overcode.model.TokenHolding;
import com.overcode.model.User;
import com.overcode.persistence.repository.interfaces.AuditLogRepository;
import com.overcode.persistence.repository.interfaces.PortfolioRepository;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.service.interfaces.TokenEmissionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
public class TokenEmissionServiceImpl implements TokenEmissionService {

    private static final int INITIAL_TOKEN_AMOUNT = 100;
    private static final BigDecimal INITIAL_PRICE = BigDecimal.valueOf(1.0);

    private final PortfolioRepository portfolioRepository;
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Value("${superuser.email:overcode@gmail.com}")
    private String superadminEmail;

    public TokenEmissionServiceImpl(PortfolioRepository portfolioRepository,
                                    AuditLogRepository auditLogRepository,
                                    UserRepository userRepository) {
        this.portfolioRepository = portfolioRepository;
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void emitTokensForNewPlayer(Player player) {
        User superadmin = userRepository.findByEmail(superadminEmail)
                .orElseThrow(() -> new IllegalStateException("Super admin not found with email: " + superadminEmail));

        Portfolio portfolio = portfolioRepository.findByUserId(superadmin.getId())
                .orElseGet(() -> new Portfolio(UUID.randomUUID(), superadmin.getId(), BigDecimal.ZERO, null));

        // Idempotency check: If the admin already has a holding for this player, do not emit again.
        if (portfolio.getHoldingForPlayer(player.getId()).isPresent()) {
            return;
        }

        // Emit tokens
        TokenHolding holding = portfolio.getOrCreateHoldingForPlayer(player.getId());
        holding.addTokens(INITIAL_TOKEN_AMOUNT);

        portfolioRepository.save(portfolio);

        // Audit Log
        AuditLog auditLog = new AuditLog(
                UUID.randomUUID(),
                AuditLog.OperationType.EMISSION,
                superadmin.getId(), // Issuer
                superadmin.getId(), // Counterparty (Self)
                player.getId(),
                INITIAL_TOKEN_AMOUNT,
                INITIAL_PRICE,
                Instant.now()
        );

        auditLogRepository.save(auditLog);
    }
}
