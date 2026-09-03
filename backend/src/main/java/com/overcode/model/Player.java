package com.overcode.model;

public class Player {

    private Long id;
    private String name;
    private Integer currentPrice;
    private Integer totalIssued;

    public Player(Long id, String name, Integer currentPrice, Integer totalIssued) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Player name is required"); // TODO error de modelo
        }
        if (currentPrice == null || currentPrice <= 0) {
            throw new IllegalArgumentException("Current price must be positive");
        }
        if (totalIssued == null || totalIssued <= 0) {
            throw new IllegalArgumentException("Total issued must be positive");
        }
        this.id = id;
        this.name = name;
        this.currentPrice = currentPrice;
        this.totalIssued = totalIssued;
    }

    public static Player create(String name, Integer currentPrice, Integer totalIssued) { // TODO borrar
        return new Player(null, name, currentPrice, totalIssued);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Integer getCurrentPrice() {
        return currentPrice;
    }

    public Integer getTotalIssued() {
        return totalIssued;
    }
}
