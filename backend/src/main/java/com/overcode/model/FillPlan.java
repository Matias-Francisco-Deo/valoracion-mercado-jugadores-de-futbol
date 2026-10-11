package com.overcode.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pure matching calculation for a market (taker) order. Given a requested quantity and a list of
 * resting counterparty orders <b>already sorted by best price</b>, it walks the book accumulating
 * quantity until the request is satisfied or the book is exhausted.
 *
 * <p>This object carries no persistence or framework concerns: the service supplies pre-filtered,
 * pre-sorted candidates and applies the resulting fills through domain mutators. The decision of
 * what to do when the book cannot fully fill the request belongs to the service layer
 * (see {@code isFullyFilled()}); the domain stays free of service-level exceptions.
 */
public class FillPlan {

    /** A single take against one resting maker order. */
    public static final class Fill {
        private final P2POrder makerOrder;
        private final int quantityTaken;

        Fill(P2POrder makerOrder, int quantityTaken) {
            this.makerOrder = makerOrder;
            this.quantityTaken = quantityTaken;
        }

        public P2POrder getMakerOrder() {
            return makerOrder;
        }

        public int getQuantityTaken() {
            return quantityTaken;
        }

        public BigDecimal getPrice() {
            return makerOrder.getPricePerToken();
        }

        public BigDecimal getSubtotal() {
            return makerOrder.getPricePerToken().multiply(BigDecimal.valueOf(quantityTaken));
        }
    }

    private final int requestedQuantity;
    private final int filledQuantity;
    private final BigDecimal totalCost;
    private final List<Fill> fills;

    private FillPlan(int requestedQuantity, int filledQuantity, BigDecimal totalCost, List<Fill> fills) {
        this.requestedQuantity = requestedQuantity;
        this.filledQuantity = filledQuantity;
        this.totalCost = totalCost;
        this.fills = fills;
    }

    public static FillPlan build(int requestedQuantity, List<P2POrder> sortedCandidates) {
        if (requestedQuantity <= 0) {
            throw new IllegalArgumentException("Requested quantity must be positive.");
        }

        List<Fill> fills = new ArrayList<>();
        BigDecimal totalCost = BigDecimal.ZERO;
        int remaining = requestedQuantity;

        for (P2POrder candidate : sortedCandidates) {
            if (remaining == 0) {
                break;
            }
            if (!candidate.isOpen()) {
                continue;
            }
            int take = Math.min(remaining, candidate.getQuantity());
            Fill fill = new Fill(candidate, take);
            fills.add(fill);
            totalCost = totalCost.add(fill.getSubtotal());
            remaining -= take;
        }

        return new FillPlan(requestedQuantity, requestedQuantity - remaining, totalCost,
                Collections.unmodifiableList(fills));
    }

    /** True when the book held enough liquidity to fully satisfy the requested quantity. */
    public boolean isFullyFilled() {
        return filledQuantity == requestedQuantity;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public int getFilledQuantity() {
        return filledQuantity;
    }

    /** Total credits the taker pays (BUY) or receives (SELL): sum of price times quantity per fill. */
    public BigDecimal totalCost() {
        return totalCost;
    }

    public List<Fill> getFills() {
        return fills;
    }
}
