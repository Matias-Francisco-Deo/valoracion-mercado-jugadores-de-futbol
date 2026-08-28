package com.overcode.market.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Player {
    private final String id;
    private final String name;
    private final BigDecimal quotation = BigDecimal.ONE;
    private final int totalSupply = 100;
    private int operatorInventory;
    private final Map<String, Integer> holders = new HashMap<>();

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
        this.operatorInventory = totalSupply;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getQuotation() { return quotation; }
    public int getTotalSupply() { return totalSupply; }
    public int getOperatorInventory() { return operatorInventory; }

    public void changeOperatorInventory(int delta) { this.operatorInventory += delta; }

    public Map<String, Integer> getHolders() { return holders; }

    public int sumUserHoldings() { return holders.values().stream().mapToInt(Integer::intValue).sum(); }

    public void addHolder(String userId, int qty) {
        holders.put(userId, holders.getOrDefault(userId, 0) + qty);
    }

    public void removeHolderUnits(String userId, int qty) {
        int current = holders.getOrDefault(userId, 0);
        int updated = Math.max(0, current - qty);
        if (updated == 0) holders.remove(userId); else holders.put(userId, updated);
    }
}
