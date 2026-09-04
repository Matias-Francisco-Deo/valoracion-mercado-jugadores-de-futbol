package com.overcode.persistency.dto;

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
public class UserJPADTO { // TODO cambiar por record?

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

    public UserJPADTO(String username, String email, String password, Integer creditBalance, Integer tokens) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.creditBalance = creditBalance;
        this.tokens = tokens;
    }

}
