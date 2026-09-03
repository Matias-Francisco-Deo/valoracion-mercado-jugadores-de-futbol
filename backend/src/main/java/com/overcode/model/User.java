package com.overcode.model;

public class User {

    private Long id;
    private String username;
    private String email;
    private String password;
    private Integer creditBalance; // TODO cambiar por tokens? o agregar créditos como otra cosa?

    public User(Long id, String username, String email, String password, Integer creditBalance) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required"); // TODO borrar
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (creditBalance == null || creditBalance < 0) {
            throw new IllegalArgumentException("Credit balance cannot be negative");
        }
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.creditBalance = creditBalance;
    }

    public static User create(String username, String email, String password) {
        return new User(null, username, email, password, 0);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Integer getCreditBalance() {
        return creditBalance;
    }

    public void setCreditBalance(Integer creditBalance) {
        if (creditBalance == null || creditBalance < 0) {
            throw new IllegalArgumentException("Credit balance cannot be negative");
        }
        this.creditBalance = creditBalance;
    }
}
