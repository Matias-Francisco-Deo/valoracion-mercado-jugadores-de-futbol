package com.overcode.market.service;

import com.overcode.market.dto.BuyTokenRequest;
import com.overcode.market.dto.BuyTokenResponse;
import com.overcode.market.dto.MarketInitializationResponse;
import com.overcode.market.model.Player;
import com.overcode.market.model.User;
import com.overcode.market.model.Wallet;
import com.overcode.market.state.MarketState;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class MarketService {
    private final MarketState state = new MarketState();

    public MarketInitializationResponse initMarket() {
        state.initDefaultMarket();
        return new MarketInitializationResponse("initialized");
    }

    public synchronized BuyTokenResponse buy(String userId, BuyTokenRequest req) {
        int qty = req.quantity();
        if (qty <= 0) throw new IllegalArgumentException("quantity must be positive");
        Player p = state.findPlayer(req.playerId());
        if (p == null) throw new IllegalArgumentException("player not found: " + req.playerId());

        User buyer = state.ensureUserExists(userId);
        Wallet wallet = buyer.getWallet();
        BigDecimal total = p.getQuotation().multiply(BigDecimal.valueOf(qty));
        if (wallet.getBalance().compareTo(total) < 0) throw new IllegalStateException("insufficient balance");
        if (p.getOperatorInventory() < qty) throw new IllegalStateException("insufficient inventory");

        // perform transfer
        wallet.debit(total);
        p.changeOperatorInventory(-qty);
        p.addHolder(userId, qty);
        buyer.addHolding(p.getId(), qty);

        return new BuyTokenResponse(userId, p.getId(), qty, buyer.getHolding(p.getId()), p.getOperatorInventory(), total);
    }
}
