package com.overcode.service.impl;

import com.overcode.model.Player;
import com.overcode.model.Portfolio;
import com.overcode.model.TokenHolding;
import com.overcode.model.User;
import com.overcode.persistence.repository.interfaces.PortfolioRepository;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.service.interfaces.TokenEmissionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class TokenEmissionServiceImpl implements TokenEmissionService {

    private static final int INITIAL_TOKEN_AMOUNT = 100;

    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;

    @Value("${superuser.email:overcode@gmail.com}")
    private String superadminEmail;

    public TokenEmissionServiceImpl(PortfolioRepository portfolioRepository,
                                    UserRepository userRepository) {
        this.portfolioRepository = portfolioRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void emitTokensForNewPlayer(Player player) {
        User superadmin = userRepository.findByEmail(superadminEmail)
                .orElseThrow(() -> new IllegalStateException("Super admin not found with email: " + superadminEmail)); // TODO exception de service

        // TODO vincular los tokens directamente con el superusuario haciendo un for y cambiando al id de owner
        //  esto es lógica de modelo, NO lo hagas desde el service (o sea, acá)

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
    }
}
