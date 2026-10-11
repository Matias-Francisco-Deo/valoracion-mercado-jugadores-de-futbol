package com.overcode.service;

import com.overcode.model.OrderType;
import com.overcode.model.Player;
import com.overcode.model.Portfolio;
import com.overcode.model.TokenHolding;
import com.overcode.model.User;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import com.overcode.persistence.repository.interfaces.PortfolioRepository;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.service.interfaces.P2POrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class BuyTokensTest {

    @Autowired
    private P2POrderService p2pOrderService;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    public void marketBuyFillsAgainstARestingSellOrder() {
        Player newPlayer = new Player();
        newPlayer.setName("Test Player " + UUID.randomUUID());
        newPlayer.setCurrentPrice(10);
        final Player player = playerRepository.guardar(newPlayer);

        // Seller holds tokens and posts a SELL order into the book (this escrows the tokens).
        User seller = userRepository.guardar(new User("seller-" + UUID.randomUUID(),
                UUID.randomUUID() + "@gmail.com", "pass"));
        Portfolio sellerPortfolio = new Portfolio(UUID.randomUUID(), seller.getId(), BigDecimal.ZERO, null);
        TokenHolding holding = new TokenHolding(UUID.randomUUID(), sellerPortfolio.getId(), player.getId(), 100);
        sellerPortfolio.addTokenHolding(holding);
        portfolioRepository.save(sellerPortfolio);

        p2pOrderService.createOrder(seller.getId(), player.getId(), OrderType.SELL, 5, new BigDecimal("10.00"));

        // Buyer with enough credits sweeps the book with just {playerId, quantity}.
        User buyer = userRepository.guardar(new User("buyer-" + UUID.randomUUID(),
                UUID.randomUUID() + "@gmail.com", "pass"));
        portfolioRepository.save(new Portfolio(UUID.randomUUID(), buyer.getId(), new BigDecimal("1000.00"), null));

        p2pOrderService.executeMarketBuy(buyer.getId(), player.getId(), 5);

        Portfolio buyerAfter = portfolioRepository.findByUserId(buyer.getId()).orElseThrow();
        boolean buyerHasTokens = buyerAfter.getTokenHoldings().stream()
                .anyMatch(h -> h.getPlayerId().equals(player.getId()) && h.getQuantity() == 5);
        assertTrue(buyerHasTokens, "buyer should hold the 5 purchased tokens");

        Portfolio sellerAfter = portfolioRepository.findByUserId(seller.getId()).orElseThrow();
        assertEquals(0, new BigDecimal("50.00").compareTo(sellerAfter.getCredits()),
                "seller should receive 5 * 10.00 credits");
    }
}
