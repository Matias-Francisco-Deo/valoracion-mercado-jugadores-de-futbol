package com.overcode.service;

import com.overcode.model.market.PeticionVenta;
import com.overcode.model.market.orden.OrdenDeCompra;
import com.overcode.model.player.Player;
import com.overcode.model.user.User;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.service.interfaces.TokenMarketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class BuyTokensTest {

    @Autowired
    private TokenMarketService tokenMarketService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    void marketBuyFillsAgainstARestingSellOrder() {
        Player newPlayer = new Player();
        newPlayer.setName("Test Player " + UUID.randomUUID());
        newPlayer.setCurrentPrice(10d);
        final Player player = playerRepository.guardar(newPlayer);

        // Seller holds tokens and creates sell orders
        User seller = userRepository.guardar(new User("seller-" + UUID.randomUUID(),
                UUID.randomUUID() + "@gmail.com", "pass"));
        if (player.getTokens() != null || seller != null) {
            seller.addTokens(player.getTokens());
        }
        userRepository.guardar(seller);

        tokenMarketService.sell(new PeticionVenta(seller, player, 5));

        // Buyer with enough credits sweeps the book
        User buyer = new User("buyer-" + UUID.randomUUID(),
                UUID.randomUUID() + "@gmail.com", "pass");
        buyer.addCredits(1000d);
        buyer = userRepository.guardar(buyer);

        tokenMarketService.buy(new OrdenDeCompra(buyer, player, 5));

        User buyerAfter = userRepository.recuperar(buyer.getId()).orElseThrow();
        assertEquals(5, buyerAfter.getTokenCountForPlayer(player.getId()), "buyer should hold the 5 purchased tokens");

        User sellerAfter = userRepository.recuperar(seller.getId()).orElseThrow();
        assertEquals(50, sellerAfter.getCreditBalance(), "seller should receive 5 * 10 credits");
    }
}
