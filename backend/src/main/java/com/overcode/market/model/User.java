package com.overcode.market.model;

import java.util.HashMap;
import java.util.Map;

public class User {
    private final String id;
    private final String name;
    private final Wallet wallet;
    private final Map<String, Integer> holdings = new HashMap<>();

    public User(String id, String name, Wallet wallet) {
        this.id = id;
        this.name = name;
        this.wallet = wallet;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Wallet getWallet() { return wallet; }

    public Map<String, Integer> getHoldings() { return holdings; }

    public int getHolding(String playerId) { return holdings.getOrDefault(playerId, 0); }

    public void addHolding(String playerId, int qty) { holdings.put(playerId, getHolding(playerId) + qty); }

    public void removeHolding(String playerId, int qty) {
        int current = getHolding(playerId);
        int updated = Math.max(0, current - qty);
        if (updated == 0) holdings.remove(playerId); else holdings.put(playerId, updated);
    }
}
