package com.overcode.service.impl;

import com.overcode.model.AuditLog;
import com.overcode.model.Player;
import com.overcode.model.Portfolio;
import com.overcode.model.TokenHolding;
import com.overcode.model.User;
import com.overcode.persistence.repository.interfaces.AuditLogRepository;
import com.overcode.persistence.repository.interfaces.PortfolioRepository;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.service.interfaces.TokenMarketService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
public class TokenMarketServiceImpl implements TokenMarketService {

    private final PortfolioRepository portfolioRepository;
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;

    @Value("${superuser.email:overcode@gmail.com}")
    private String superadminEmail;

    public TokenMarketServiceImpl(PortfolioRepository portfolioRepository,
                                  AuditLogRepository auditLogRepository,
                                  UserRepository userRepository,
                                  PlayerRepository playerRepository) {
        this.portfolioRepository = portfolioRepository;
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    @Transactional
    public void buyTokens(Long userId, Long playerId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        // 1. Fetch User and Admin
        User superadmin = userRepository.findByEmail(superadminEmail)
                .orElseThrow(() -> new IllegalStateException("Super admin not found"));
        
        if (userId.equals(superadmin.getId())) {
            throw new IllegalArgumentException("Super admin cannot buy from themselves");
        }

        // 2. Fetch Player to get price
        Player player = playerRepository.recuperar(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));
        
        BigDecimal pricePerToken = BigDecimal.valueOf(player.getCurrentPrice());
        BigDecimal totalCost = pricePerToken.multiply(BigDecimal.valueOf(quantity));

        // 3. Load Portfolios
        Portfolio buyerPortfolio = portfolioRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Buyer portfolio not found"));
        
        Portfolio adminPortfolio = portfolioRepository.findByUserId(superadmin.getId())
                .orElseThrow(() -> new IllegalStateException("Admin portfolio not found"));

        // 4. Validate Funds and Tokens
        if (!buyerPortfolio.hasSufficientCredits(totalCost)) {
            throw new IllegalStateException("Insufficient funds");
        }

        TokenHolding adminHolding = adminPortfolio.getHoldingForPlayer(playerId)
                .orElseThrow(() -> new IllegalStateException("Admin has no tokens for this player"));
        
        if (!adminHolding.hasSufficientTokens(quantity)) {
            throw new IllegalStateException("Not enough tokens available in the market");
        }

        // 5. Execute Trade
        buyerPortfolio.deductCredits(totalCost);
        adminPortfolio.addCredits(totalCost);

        adminHolding.deductTokens(quantity);
        TokenHolding buyerHolding = buyerPortfolio.getOrCreateHoldingForPlayer(playerId);
        buyerHolding.addTokens(quantity);

        // 6. Save Portfolios
        portfolioRepository.save(buyerPortfolio);
        portfolioRepository.save(adminPortfolio);

        // 7. Audit Log
        AuditLog auditLog = new AuditLog(
                UUID.randomUUID(),
                AuditLog.OperationType.BUY,
                userId,
                superadmin.getId(),
                playerId,
                quantity,
                pricePerToken,
                Instant.now()
        );
        auditLogRepository.save(auditLog);
    }

    @Override
    @Transactional
    public void sellTokens(Long userId, Long playerId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        User superadmin = userRepository.findByEmail(superadminEmail)
                .orElseThrow(() -> new IllegalStateException("Super admin not found"));
        
        if (userId.equals(superadmin.getId())) {
            throw new IllegalArgumentException("Super admin cannot sell to themselves");
        }

        Player player = playerRepository.recuperar(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));
        
        BigDecimal pricePerToken = BigDecimal.valueOf(player.getCurrentPrice());
        BigDecimal totalProceeds = pricePerToken.multiply(BigDecimal.valueOf(quantity));

        Portfolio sellerPortfolio = portfolioRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Seller portfolio not found"));
        
        Portfolio adminPortfolio = portfolioRepository.findByUserId(superadmin.getId())
                .orElseThrow(() -> new IllegalStateException("Admin portfolio not found"));

        TokenHolding sellerHolding = sellerPortfolio.getHoldingForPlayer(playerId)
                .orElseThrow(() -> new IllegalStateException("You don't own any tokens for this player"));
        
        if (!sellerHolding.hasSufficientTokens(quantity)) {
            throw new IllegalStateException("Not enough tokens to sell");
        }

        adminPortfolio.deductCredits(totalProceeds);
        sellerPortfolio.addCredits(totalProceeds);

        sellerHolding.deductTokens(quantity);
        TokenHolding adminHolding = adminPortfolio.getOrCreateHoldingForPlayer(playerId);
        adminHolding.addTokens(quantity);

        portfolioRepository.save(sellerPortfolio);
        portfolioRepository.save(adminPortfolio);

        AuditLog auditLog = new AuditLog(
                UUID.randomUUID(),
                AuditLog.OperationType.SELL,
                userId,
                superadmin.getId(),
                playerId,
                quantity,
                pricePerToken,
                Instant.now()
        );
        auditLogRepository.save(auditLog);
    }
}
