package com.overcode.persistency.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "players")
public class PlayerRecord { // TODO cambiar por record?

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "current_price", nullable = false)
    private Integer currentPrice;

    @Column(name = "total_issued", nullable = false)
    private Integer totalIssued;

    protected PlayerRecord() {
    }

    public PlayerRecord(String name, Integer currentPrice, Integer totalIssued) {
        this.name = name;
        this.currentPrice = currentPrice;
        this.totalIssued = totalIssued;
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

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Integer currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Integer getTotalIssued() {
        return totalIssued;
    }

    public void setTotalIssued(Integer totalIssued) {
        this.totalIssued = totalIssued;
    }
}
