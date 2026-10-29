package com.overcode.model.user;

import com.overcode.model.exception.FondosInsuficientesException;
import com.overcode.model.exception.ParametroNoValidoException;
import com.overcode.model.exception.TokensInsuficientesParaVenderException;
import com.overcode.model.player.Player;
import com.overcode.model.token.Token;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
        if (amount <= 0) {
            throw new ParametroNoValidoException("Cantidad debe ser positiva");
        }
        if (!hasSufficientCredits(amount)) {
            throw new FondosInsuficientesException("Fondos insuficientes");
        }
        this.creditBalance -= amount;
    }

    public void creditCredits(Double amount) {
        if (amount == null || amount <= 0) {
            throw new ParametroNoValidoException("Cantidad debe ser positiva");
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
            throw new ParametroNoValidoException("Quantity must be positive.");
        }
        List<Token> playerTokens = getTokensForPlayer(player.getId());
        if (playerTokens.size() < quantity) {
            throw new TokensInsuficientesParaVenderException("No hay suficientes tokens para vender la cantidad pedida");
        }

        List<Token> toRelease = new ArrayList<>(playerTokens.subList(0, quantity));
        this.tokens.removeAll(toRelease);
        return toRelease;
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
        newTokens.forEach(this::addToken);
    }

}
