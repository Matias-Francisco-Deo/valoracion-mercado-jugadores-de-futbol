package com.overcode.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter @NoArgsConstructor
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
        setCreditBalance(creditBalance);
        setTokens(tokens);
    }

    public User(String username, String email, String password, Integer creditBalance, Integer tokens) {
        this.username = username;
        this.email = email;
        this.password = password;
        setCreditBalance(creditBalance);
        this.tokens = tokens;
    }

    public void setCreditBalance(Integer creditBalance) {
        this.creditBalance = Math.max(creditBalance, 0);
    }

    public void setTokens(Integer tokens) {
        this.tokens = Math.max(tokens, 0);
    }
}
