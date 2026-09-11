package com.overcode.model;

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
    private Integer creditBalance;
    private List<Token> tokens;

    public User(Long id, String username, String email, String password, Integer creditBalance, List<Token> tokens) {
        setId(id);
        setUsername(username);
        setEmail(email);
        setPassword(password);
        setCreditBalance(creditBalance);
        setTokens(tokens);
    }

    public User(String username, String email, String password, Integer creditBalance, List<Token> tokens) {
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
        setCreditBalance(0);
        setTokens(new ArrayList<>());
    }

    public void setCreditBalance(Integer creditBalance) {
        this.creditBalance = Math.max(creditBalance, 0);
    }


}
