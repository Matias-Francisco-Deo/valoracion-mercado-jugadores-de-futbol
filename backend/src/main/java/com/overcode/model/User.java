package com.overcode.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class User {

    private Long id;
    private String username;
    private String email;
    private String password;
    private Integer creditBalance; // TODO preguntar esto
    private Integer tokens;

    public User(Long id, String username, String email, String password, Integer creditBalance, Integer tokens) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.creditBalance = creditBalance;
        this.tokens = tokens;
    }

    public void setCreditBalance(Integer creditBalance) {
        if (creditBalance == null || creditBalance < 0) {
            throw new IllegalArgumentException("Credit balance cannot be negative");
        }
        this.creditBalance = creditBalance;
    }

    public void setTokenBalance(Integer tokens) {
        if (tokens == null || tokens < 0) {
            throw new IllegalArgumentException("Token balance cannot be negative");
        }
        this.tokens = tokens;
    }
}
