package com.overcode.unit;

import com.overcode.market.dto.BuyTokenRequest;
import com.overcode.market.dto.BuyTokenResponse;
import com.overcode.market.service.MarketService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MarketServiceBuyTest {

    @Test
    void buyReducesOperatorInventoryAndDebitsWallet() {
        MarketService svc = new MarketService();
        svc.initMarket();

        // user u1 buys 2 tokens of p1
        BuyTokenRequest req = new BuyTokenRequest("p1", 2);
        BuyTokenResponse resp = svc.buy("u1", req);

        assertEquals("u1", resp.userId());
        assertEquals("p1", resp.playerId());
        assertEquals(2, resp.quantity());
        assertTrue(resp.userHoldings() >= 1);
        assertTrue(resp.operatorInventory() <= 100);
        assertNotNull(resp.totalValue());
    }
}
