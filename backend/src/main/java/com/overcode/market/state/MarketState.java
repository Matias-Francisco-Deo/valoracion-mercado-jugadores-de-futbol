package com.overcode.market.state;

import com.overcode.market.model.Player;
import com.overcode.market.model.User;
import com.overcode.market.model.Wallet;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MarketState {
    private final Map<String, Player> players = new HashMap<>();
    private final Map<String, User> users = new HashMap<>();

    public synchronized void initDefaultMarket() {
        players.clear();
        users.clear();
        // Create example players
        Player p1 = new Player("p1", "Lionel Messi");
        Player p2 = new Player("p2", "Cristiano Ronaldo");
        players.put(p1.getId(), p1);
        players.put(p2.getId(), p2);

        // Create an example operator as a user with id operator
        Wallet operatorWallet = new Wallet("w-operator", "operator", BigDecimal.ZERO);
        User operator = new User("operator", "Market Operator", operatorWallet);
        users.put(operator.getId(), operator);
    }

    public Map<String, Player> getPlayers() { return Collections.unmodifiableMap(players); }
    public Map<String, User> getUsers() { return Collections.unmodifiableMap(users); }

    public synchronized User ensureUserExists(String userId) {
        return users.computeIfAbsent(userId, id -> new User(id, "user-"+id, new Wallet("w-"+id, id, BigDecimal.valueOf(1000)))) ;
    }

    public Player findPlayer(String playerId) { return players.get(playerId); }
}
