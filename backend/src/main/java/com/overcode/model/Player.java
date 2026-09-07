package com.overcode.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Player {

    private Long id;
    private String name;
    private Integer currentPrice;
    private Integer totalTokensIssued; // TODO tokens como objeto aparte o no?

    public Player(Long id, String name) {
        this.id = id;
        this.name = name;
        setCurrentPrice(1);
        setTotalTokensIssued(100);
    }

    public Player(String name) {
        this(null, name);
    }

    public Player(Long id, String name, Integer currentPrice, Integer totalTokensIssued) {
        this.id = id;
        this.name = name;
        setCurrentPrice(currentPrice);
        setTotalTokensIssued(totalTokensIssued);
    }

    public void setCurrentPrice(Integer currentPrice) {
        this.currentPrice = Math.max(currentPrice, 1);
    }

    public void setTotalTokensIssued(Integer totalTokensIssued) {
        this.totalTokensIssued = Math.max(totalTokensIssued, 0);
    }
}
