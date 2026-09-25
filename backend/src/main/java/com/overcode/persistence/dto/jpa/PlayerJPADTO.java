package com.overcode.persistence.dto.jpa;

import com.overcode.model.Player;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity(name="player")
@Table(name = "players")
@Setter
@Getter
@NoArgsConstructor
public class PlayerJPADTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = true, unique = true) // TODO índices? null?
    private Long externalId; // TODO tiene sentido? o es raro tener el ID de otros adentro de la db? porque esto haría más rápido el proceso

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "current_price", nullable = false)
    private Integer currentPrice;

    @Column(name = "club_name")
    private String clubName;

    @Column(name = "goals")
    private Integer goals;
    @Column(name = "assists")
    private Integer assists;
    @Column(name = "shots_on_target")
    private Integer shotsOnTarget;
    @Column(name = "passes")
    private Integer passes;

    @Column(name = "interceptions")
    private Integer interceptions;
    @Column(name = "tackles")
    private Integer tackles;
    @Column(name = "key_passes")
    private Integer keyPasses;
    @Column(name = "rating")
    private Double rating;
    @Column(name = "successful_dribbles")
    private Integer successfulDribbles;


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
        PlayerJPADTO dto = new PlayerJPADTO();
        dto.setId(player.getId());
        dto.setName(player.getName());
        dto.setCurrentPrice(player.getCurrentPrice());
        dto.setTokens(TokenJPADTO.desdeModelo(player.getTokens(), dto));
        dto.setClubName(player.getClubName());
        dto.setExternalId(player.getExternalId());
        dto.setGoals(player.getGoals());
        dto.setAssists(player.getAssists());
        dto.setShotsOnTarget(player.getShotsOnTarget());
        dto.setPasses(player.getPasses());
        dto.setInterceptions(player.getInterceptions());
        dto.setTackles(player.getTackles());
        dto.setKeyPasses(player.getKeyPasses());
        dto.setRating(player.getRating());
        dto.setSuccessfulDribbles(player.getSuccessfulDribbles());

        return dto;
    }

    public Player aModelo() {
        Player player = new Player();
        player.setId(this.id);
        player.setExternalId(this.getExternalId());
        player.setName(this.name);
        player.setCurrentPrice(this.currentPrice);
        player.setTokens(this.tokens.stream().map(token -> token.aModelo(player)).toList());
        player.setClubName(this.clubName);
        player.setGoals(this.goals);
        player.setAssists(this.assists);
        player.setShotsOnTarget(this.shotsOnTarget);
        player.setPasses(this.passes);
        player.setInterceptions(this.interceptions);
        player.setTackles(this.tackles);
        player.setKeyPasses(this.keyPasses);
        player.setRating(this.rating);
        player.setSuccessfulDribbles(this.successfulDribbles);
        return player;
    }
}
