package com.overcode.persistence.dto.jpa;

import com.overcode.model.PlayerGameData;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name="player_game_data")
@Table(name = "players_game_data")
@Setter
@Getter
@NoArgsConstructor
public class PlayerGameDataJPADTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "player")
    private PlayerJPADTO player;

    @Column(nullable = false, name = "goals")
    private Integer goals;

    @Column(nullable = false, name = "assists")
    private Integer assists;

    @Column(nullable = false, name = "shots_on_target")
    private Integer shotsOnTarget;

    @Column(nullable = false, name = "tackles")
    private Integer tackles;

    @Column(nullable = false, name = "key_passes")
    private Integer keyPasses;

    @Column(nullable = false, name = "rating")
    private Double rating;

    @Column(nullable = false, name = "successful_dribbles")
    private Integer successfulDribbles;


    public PlayerGameDataJPADTO(Long id, PlayerJPADTO player, Integer goals, Integer assists,
                                Integer shotsOnTarget, Integer tackles,
                                Integer keyPasses, Double rating, Integer successfulDribbles) {
        setId(id);
        setPlayer(player);
        setGoals(goals);
        setAssists(assists);
        setShotsOnTarget(shotsOnTarget);
        setTackles(tackles);
        setKeyPasses(keyPasses);
        setRating(rating);
        setSuccessfulDribbles(successfulDribbles);
    }

    public static PlayerGameDataJPADTO desdeModelo(PlayerGameData playerGameData) {
        if (playerGameData == null) {
            return null;
        }
        PlayerGameDataJPADTO dto = new PlayerGameDataJPADTO();
        dto.setPlayer(PlayerJPADTO.desdeModelo(playerGameData.getPlayer())); // TODO ver recursión
        dto.setId(playerGameData.getId());
        dto.setGoals(playerGameData.getGoals());
        dto.setAssists(playerGameData.getAssists());
        dto.setShotsOnTarget(playerGameData.getShotsOnTarget());
        dto.setTackles(playerGameData.getTackles());
        dto.setKeyPasses(playerGameData.getKeyPasses());
        dto.setRating(playerGameData.getRating());
        dto.setSuccessfulDribbles(playerGameData.getSuccessfulDribbles());
        return dto;
    }

    public PlayerGameData aModelo() {
        PlayerGameData playerGameData = new PlayerGameData();
        playerGameData.setId(this.id);
        playerGameData.setGoals(this.goals);
        playerGameData.setAssists(this.assists);
        playerGameData.setShotsOnTarget(this.shotsOnTarget);
        playerGameData.setTackles(this.tackles);
        playerGameData.setKeyPasses(this.keyPasses);
        playerGameData.setRating(this.rating);
        playerGameData.setSuccessfulDribbles(this.successfulDribbles);
        return playerGameData;
    }
}


