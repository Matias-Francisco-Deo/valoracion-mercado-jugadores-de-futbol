package com.overcode.model.user;

import com.overcode.model.exception.FondosInsuficientesException;
import com.overcode.model.exception.ParametroNoValidoException;
import com.overcode.model.exception.TokensInsuficientesParaVenderException;
import com.overcode.model.bonus.BonusClaim;
import com.overcode.model.bonus.EstadoBonus;
import com.overcode.model.bonus.LoginBonus;
import com.overcode.model.player.Player;
import com.overcode.model.token.Token;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
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

    public User(Long id, String username, String email, String password) {
        setId(id);
        setUsername(username);
        setEmail(email);
        setPassword(password);
        setCreditBalance(0d);
        setTokens(new ArrayList<>());
    }

    public User(Long userId) {
        setId(userId);
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

    public void addCredits(Double amount) { // TODO arreglar
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


    public BonusClaim reclamarBonusDiario(LoginBonus loginBonus) {
        actualizarRacha();
        Double bonus = loginBonus.getBonusParaUser(this);
        Integer racha = aumentarRacha();
        addCredits(bonus);
        return new BonusClaim(bonus, racha, getCreditBalance());
    }

    public LocalDate getFechaActual() {
        return LocalDate.now(ZoneOffset.UTC);
    }

    public Boolean yaReclamoBonus(LocalDate hoy) {
        return this.lastBonusClaimDate != null && this.lastBonusClaimDate.equals(hoy);
    }

    public void actualizarRacha() {
        if (getLastBonusClaimDate() == null) {setStreakCount(0); return;}
        long diasEntreReclamos = ChronoUnit.DAYS.between(getLastBonusClaimDate(), getFechaActual());
        if (diasEntreReclamos > 1) {
            setStreakCount(0);
        }
    }

    public Integer aumentarRacha() {
        Integer nuevaRacha = calcularSiguienteRacha();
        setStreakCount(nuevaRacha);
        setLastBonusClaimDate(getFechaActual());
        return nuevaRacha;
    }

    public Integer calcularSiguienteRacha() {
        Integer rachaActual = getStreakCount();
        if (rachaActual == 6) return 0;
        return rachaActual + 1;
    }

    public EstadoBonus obtenerEstadoBonus(LoginBonus loginBonus) {
        return loginBonus.obtenerEstadoBonus(this);
    }
}
