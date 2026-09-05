package com.overcode.persistence.dto;

import com.overcode.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserJPADTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "credit_balance", nullable = false)
    private Integer creditBalance;

    @Column(name = "tokens", nullable = false)
    private Integer tokens;

    public UserJPADTO(Long id, String username, String email, String password, Integer creditBalance, Integer tokens) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.creditBalance = creditBalance;
        this.tokens = tokens;
    }

    public UserJPADTO(String username, String email, String password, Integer creditBalance, Integer tokens) {
        this(null, username, email, password, creditBalance, tokens);
    }

    public UserJPADTO(String username, String email, String password, Integer creditBalance) {
        this(null, username, email, password, creditBalance, 0);
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
