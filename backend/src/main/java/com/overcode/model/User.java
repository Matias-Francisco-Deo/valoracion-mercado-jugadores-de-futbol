package com.overcode.model;

import com.overcode.model.bonus.BonusClaim;
import com.overcode.model.bonus.LoginBonus;
import com.overcode.model.player.Player;
import com.overcode.model.token.Token;
import com.overcode.model.transaction.Transaction;
import com.overcode.model.transaction.TransactionType;
import com.overcode.service.exception.InsufficientLiquidityException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
    private Integer streakCount = 0;
    private LocalDate lastBonusClaimDate;

    public User(Long id, String username, String email, String password, Double creditBalance, List<Token> tokens) {
        setId(id);
        setUsername(username);
        setEmail(email);
        setPassword(password);
        setCreditBalance(creditBalance);
        setTokens(tokens);
        setStreakCount(0);
    }

    public User(String username, String email, String password, Double creditBalance, List<Token> tokens) {
        setUsername(username);
        setEmail(email);
        setPassword(password);
        setCreditBalance(creditBalance);
        setTokens(tokens);
        setStreakCount(0);
    }

    public User(String username, String email, String password) {
        setUsername(username);
        setEmail(email);
        setPassword(password);
        setCreditBalance(0d);
        setTokens(new ArrayList<>());
        setStreakCount(0);
    }

    public User(Long userId) {
        setId(userId);
    }

    public Integer getStreakCount() {
        return this.streakCount != null ? this.streakCount : 0;
    }

    public void setStreakCount(Integer streakCount) {
        this.streakCount = streakCount != null ? streakCount : 0;
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

    public void addCredits(Double amount) { // TODO arreglar
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
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }
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

    public List<SellOrder> sellTokens(Player player, int quantity) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");//TODO crear una exepcion de modelo
        }

        List<Token> userTokens =  releaseTokensForSale(player, quantity);
        List<Token> tokensToSell = player.getTokensFrom(userTokens);

        return tokensToSell.stream().map(token -> token.vender(player.getCurrentPrice())).toList();
    }

    public List<Transaction> buyTokens(List<SellOrder> orders) {
        if (orders.isEmpty()) {throw new InsufficientLiquidityException("Not enough tokens to buy.");}

        Map<Long, User> sellersById = new HashMap<>();

        for (SellOrder order : orders) {
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

    private List<Transaction> createTransactions(List<SellOrder> orders) {//crear un objeto aparte para crear las transacciones?
        List<Transaction> transactions = new ArrayList<>();

        Player player = orders.get(0).getPlayer();

        transactions.add(new Transaction(this, player, TransactionType.BUY, orders.size()));

        Map<User, Integer> quantityBySeller = orders.stream()
                .collect(Collectors.groupingBy(
                        SellOrder::getOwner,
                        Collectors.summingInt(order -> 1)
                ));

        quantityBySeller.forEach((seller, quantity) ->
                transactions.add(new Transaction(seller, player, TransactionType.SELL, quantity))
        );

        return transactions;
    }


    public BonusClaim reclamarBonusDiario(LoginBonus loginBonus) {
        return loginBonus.getBonusParaUser(this);
    }

    public Boolean yaReclamoBonus(LocalDate hoy) {
        return this.lastBonusClaimDate != null && this.lastBonusClaimDate.equals(hoy);
    }

    public void actualizarRacha(LocalDate dia) {
        if (getLastBonusClaimDate() == null) {setStreakCount(0); return;}
        long diasEntreReclamos = ChronoUnit.DAYS.between(getLastBonusClaimDate(), dia);
        if (diasEntreReclamos > 1) {
            setStreakCount(0);
        }
    }

    public Integer aumentarRacha(LocalDate dia) {
        Integer nuevaRacha = calcularSiguienteRacha();
        setStreakCount(nuevaRacha);
        setLastBonusClaimDate(dia);
        return nuevaRacha;
    }

    public Integer calcularSiguienteRacha() {
        Integer rachaActual = getStreakCount();
        if (rachaActual == 6) return 0;
        return rachaActual + 1;
    }
}
