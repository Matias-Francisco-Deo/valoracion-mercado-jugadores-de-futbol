package com.overcode.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Player {

    private Long id;
    private String name;
    private Integer currentPrice;
    private Integer totalIssued;

    public Player(Long id, String name, Integer currentPrice, Integer totalIssued) {
        this.id = id;
        this.name = name;
        this.currentPrice = currentPrice;
        this.totalIssued = totalIssued;
    }
}
