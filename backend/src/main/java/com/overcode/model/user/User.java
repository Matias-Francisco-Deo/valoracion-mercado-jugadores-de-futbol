package com.overcode.model.user;

import com.overcode.model.player.Player;
import com.overcode.model.token.Token;
import com.overcode.model.venta.OrdenDeCompra;
import com.overcode.model.venta.transaction.TransaccionFinanciera;
import com.overcode.model.venta.transaction.TransactionType;
import com.overcode.service.exception.InsufficientLiquidityException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Setter @Getter @NoArgsConstructor
public class User {

    private Long id;
    private String username;
    private String email;
    private String password;
    private Double creditBalance;
    private List<Token> tokens;

    public User(Long id, String username, String email, String password, Double creditBalance, List<Token> tokens) {
        setId(id);
        setUsername(username);
        setEmail(email);
        setPassword(password);
        setCreditBalance(creditBalance);
        setTokens(tokens);
    }

    public User(String username, String email, String password, Double creditBalance, List<Token> tokens) {
        setUsername(username);
        setEmail(email);
        setPassword(password);
        setCreditBalance(creditBalance);
        setTokens(tokens);
    }

    public User(String username, String email, String password) {
        setUsername(username);
        setEmail(email);
        setPassword(password);
        setCreditBalance(0d);
        setTokens(new ArrayList<>());
    }

    public User(Long id, String username, String email, String password) {
        setId(id);
        setUsername(username);
        setEmail(email);
        setPassword(password);
        setCreditBalance(0d);
        setTokens(new ArrayList<>());
    }

    public void setCreditBalance(Double creditBalance) {
        this.creditBalance = creditBalance != null ? Math.max(creditBalance, 0) : 0;
    }

    public boolean hasSufficientCredits(Double amount) {
        return amount != null && amount > 0 && this.creditBalance != null && this.creditBalance >= amount;
    }

    public void debitCredits(Double amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (!hasSufficientCredits(amount)) {
            throw new com.overcode.model.exception.InsufficientFundsException("Insufficient funds");
        }
        this.creditBalance -= amount;
    }

    public void creditCredits(Double amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.creditBalance = (this.creditBalance != null ? this.creditBalance : 0) + amount;
    }

    public List<Token> getTokensForPlayer(Long playerId) {
        if (this.tokens == null || playerId == null) {
            return new ArrayList<>();
        }
        return this.tokens.stream()
                .filter(t -> t.getPlayer() != null && playerId.equals(t.getPlayer().getId()))
                .toList();
    }

    public int getTokenCountForPlayer(Long playerId) {
        return getTokensForPlayer(playerId).size();
    }

    public List<Token> releaseTokensForSale(Player player, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        List<Token> playerTokens = getTokensForPlayer(player.getId());
        if (playerTokens.size() < quantity) {
            throw new com.overcode.service.exception.InsufficientEscrowException("Not enough tokens to place SELL order.");
        }

        List<Token> toRelease = new ArrayList<>(playerTokens.subList(0, quantity));
        this.tokens.removeAll(toRelease);
        return toRelease;
    }

    public List<OrdenDeCompra> sellTokens(Player player, int quantity) {
        List<Token> userTokens =  releaseTokensForSale(player, quantity);
        List<Token> tokensToSell = player.getTokensFrom(userTokens);

        return tokensToSell.stream().map(Token::vender).toList();
    }

    public List<TransaccionFinanciera> buyTokens(List<OrdenDeCompra> orders) {
        if (orders.isEmpty()) {throw new InsufficientLiquidityException("Not enough tokens to buy.");}

        Map<Long, User> sellersById = new HashMap<>();

        for (OrdenDeCompra order : orders) {
            User seller = order.getOwner();

            User existingSeller = sellersById.putIfAbsent(seller.getId(), seller);

            if (existingSeller != null) {order.setOwner(existingSeller);}

            order.executePurchase(this);
        }

        return createTransactions(orders);
    }

    public void addToken(Token token) {
        if (token == null) {
            return;
        }
        if (this.tokens == null) {
            this.tokens = new ArrayList<>();
        }
        this.tokens.add(token);
        token.setOwner(this);
    }

    public void addTokens(List<Token> newTokens) {
        if (newTokens == null) {
            return;
        }
        for (Token token : newTokens) {
            addToken(token);
        }
    }

    private List<TransaccionFinanciera> createTransactions(List<OrdenDeCompra> orders) {//crear un objeto aparte para crear las transacciones?
        List<TransaccionFinanciera> transaccionFinancieras = new ArrayList<>();

        Player player = orders.get(0).getPlayer();

        transaccionFinancieras.add(new TransaccionFinanciera(this, player, TransactionType.BUY, orders.size()));

        Map<User, Integer> quantityBySeller = orders.stream()
                .collect(Collectors.groupingBy(
                        OrdenDeCompra::getOwner,
                        Collectors.summingInt(order -> 1)
                ));

        quantityBySeller.forEach((seller, quantity) ->
                transaccionFinancieras.add(new TransaccionFinanciera(seller, player, TransactionType.SELL, quantity))
        );

        return transaccionFinancieras;
    }

}
