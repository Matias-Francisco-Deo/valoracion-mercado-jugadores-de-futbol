package com.overcode.persistence.dto;

import com.overcode.model.Player;
import com.overcode.model.Token;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity(name="player")
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

    @Column(name = "tokens", nullable = false)
    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TokenJPADTO> tokens = new ArrayList<>();

    public PlayerJPADTO(Long id, String name, Integer currentPrice, List<TokenJPADTO> tokens) {
        setId(id);
        setName(name);
        setCurrentPrice(currentPrice);
        setTokens(tokens);
    }

    public static PlayerJPADTO desdeModelo(Player player) {
        if (player == null) {
            return null;
        }
        return new PlayerJPADTO(
            player.getId(),
            player.getName(),
            player.getCurrentPrice(),
            TokenJPADTO.desdeModelo(player.getTokens())
        );
    }

    public Player aModelo() {
        return new Player(
            this.id,
            this.name,
            this.currentPrice,
                this.tokens.stream().map(TokenJPADTO::aModelo).collect(Collectors.toList())
        );
    }
}
