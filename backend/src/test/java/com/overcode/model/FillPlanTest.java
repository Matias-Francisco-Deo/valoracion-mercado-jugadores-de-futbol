package com.overcode.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FillPlanTest {

    private P2POrder sell(long userId, int quantity, String price) {
        return P2POrder.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .playerId(10L)
                .type(OrderType.SELL)
                .quantity(quantity)
                .pricePerToken(new BigDecimal(price))
                .status(OrderStatus.OPEN)
                .createdAt(Instant.now())
                .version(null)
                .build();
    }

    @Test
    void fillsFromASingleOrderWhenItCovers() {
        FillPlan plan = FillPlan.build(3, List.of(sell(2L, 10, "2.00")));

        assertTrue(plan.isFullyFilled());
        assertEquals(1, plan.getFills().size());
        assertEquals(3, plan.getFills().get(0).getQuantityTaken());
        assertEquals(0, new BigDecimal("6.00").compareTo(plan.totalCost()));
    }

    @Test
    void sweepsMultipleOrdersAtTheirOwnPrices() {
        // Candidates arrive already sorted best-price-first (cheapest sell first).
        FillPlan plan = FillPlan.build(12, List.of(
                sell(2L, 5, "1.00"),
                sell(3L, 5, "2.00"),
                sell(4L, 10, "3.00")));

        assertTrue(plan.isFullyFilled());
        assertEquals(3, plan.getFills().size());
        assertEquals(2, plan.getFills().get(2).getQuantityTaken()); // only 2 taken from the last order
        // 5*1 + 5*2 + 2*3 = 21
        assertEquals(0, new BigDecimal("21.00").compareTo(plan.totalCost()));
        assertEquals(12, plan.getFilledQuantity());
    }

    @Test
    void stopsOnceRequestedQuantityIsReached() {
        FillPlan plan = FillPlan.build(5, List.of(
                sell(2L, 5, "1.00"),
                sell(3L, 5, "2.00")));

        assertEquals(1, plan.getFills().size());
        assertEquals(0, new BigDecimal("5.00").compareTo(plan.totalCost()));
    }

    @Test
    void notFullyFilledWhenBookIsTooThin() {
        FillPlan plan = FillPlan.build(20, List.of(
                sell(2L, 5, "1.00"),
                sell(3L, 5, "2.00")));

        assertFalse(plan.isFullyFilled());
        assertEquals(10, plan.getFilledQuantity());
    }

    @Test
    void notFullyFilledWhenBookIsEmpty() {
        FillPlan plan = FillPlan.build(5, List.of());
        assertFalse(plan.isFullyFilled());
        assertEquals(0, plan.getFilledQuantity());
        assertTrue(plan.getFills().isEmpty());
    }

    @Test
    void skipsNonOpenCandidates() {
        P2POrder completed = sell(2L, 10, "1.00");
        completed.markCompleted();
        FillPlan plan = FillPlan.build(3, List.of(completed, sell(3L, 10, "2.00")));

        assertTrue(plan.isFullyFilled());
        assertEquals(1, plan.getFills().size());
        assertEquals(0, new BigDecimal("6.00").compareTo(plan.totalCost()));
    }

    @Test
    void rejectsNonPositiveRequest() {
        assertThrows(IllegalArgumentException.class, () -> FillPlan.build(0, List.of()));
    }
}
