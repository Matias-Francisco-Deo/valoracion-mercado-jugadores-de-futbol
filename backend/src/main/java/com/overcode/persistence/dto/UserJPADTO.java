package com.overcode.persistence.dto;

import com.overcode.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public record UserJPADTO(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id,

    @Column(nullable = false, unique = true)
    String username,

    @Column(nullable = false, unique = true)
    String email,

    @Column(nullable = false)
    String password,

    @Column(name = "credit_balance", nullable = false)
    Integer creditBalance,

    @Column(name = "tokens", nullable = false)
    Integer tokens
) {

    public UserJPADTO(String username, String email, String password, Integer creditBalance, Integer tokens) {
        this(null, username, email, password, creditBalance, tokens);
    }

    public static UserJPADTO desdeModelo(User user) {
        if (user == null) {
            return null;
        }
        return new UserJPADTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getPassword(),
            user.getCreditBalance(),
            user.getTokens()
        );
    }

    public User aModelo() {
        return new User(
            this.id,
            this.username,
            this.email,
            this.password,
            this.creditBalance,
            this.tokens
        );
    }
}
