package com.overcode.market.dto;

import java.math.BigDecimal;

public record SellTokenResponse(String userId, String playerId, int quantity, int userHoldings, int operatorInventory, BigDecimal totalValue) { }
