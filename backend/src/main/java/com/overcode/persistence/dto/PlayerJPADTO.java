package com.overcode.persistence.dto;

import com.overcode.model.Player;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "players")
@Setter
@Getter
@NoArgsConstructor
public class PlayerJPADTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "current_price", nullable = false)
    private Integer currentPrice;

    @Column(name = "total_tokens_issued", nullable = false)
    private Integer totalTokensIssued;

    public PlayerJPADTO(Long id, String name, Integer currentPrice, Integer totalTokensIssued) {
        this.id = id;
        this.name = name;
        this.currentPrice = currentPrice;
        this.totalTokensIssued = totalTokensIssued;
    }

    public static PlayerJPADTO desdeModelo(Player player) {
        if (player == null) {
            return null;
        }
        return new PlayerJPADTO(
            player.getId(),
            player.getName(),
            player.getCurrentPrice(),
            player.getTotalTokensIssued()
        );
    }

    public Player aModelo() {
        return new Player(
            this.id,
            this.name,
            this.currentPrice,
            this.totalTokensIssued
        );
    }
}
