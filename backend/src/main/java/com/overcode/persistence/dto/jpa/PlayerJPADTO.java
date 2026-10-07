package com.overcode.persistence.dto.jpa;

import com.overcode.model.Player;
import com.overcode.model.Team;
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
    @Column(name = "external_id", unique = true)
    private Long externalId;

    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false, name = "current_price")
    private Integer currentPrice;


    @Column(name = "tokens", nullable = false)
    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TokenJPADTO> tokens = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    private TeamJPADTO team;

    @OneToOne(mappedBy = "player", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private PlayerGameDataJPADTO playerGameData;


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
        dto.setExternalId(player.getExternalId());

        dto.setPlayerGameData(PlayerGameDataJPADTO.desdeModelo(player.getPlayerGameData(), dto));
        dto.setTeam(player.getTeam() != null ? TeamJPADTO.desdeModeloSinJugadores(player.getTeam()) : null);

        return dto;
    }

    public static PlayerJPADTO desdeModelo(Player player, PlayerGameDataJPADTO playerGameDataJPADTO) {
        if (player == null) {
            return null;
        }
        PlayerJPADTO dto = new PlayerJPADTO();
        dto.setId(player.getId());
        dto.setName(player.getName());
        dto.setCurrentPrice(player.getCurrentPrice());
        dto.setTokens(TokenJPADTO.desdeModelo(player.getTokens(), dto));
        dto.setExternalId(player.getExternalId());

        dto.setPlayerGameData(playerGameDataJPADTO);
        dto.setTeam(player.getTeam() != null ? TeamJPADTO.desdeModeloSinJugadores(player.getTeam()) : null);

        return dto;
    }

    public static PlayerJPADTO desdeModelo(Player player, TeamJPADTO teamJPADTO) {
        if (player == null) {
            return null;
        }
        PlayerJPADTO dto = new PlayerJPADTO();
        dto.setId(player.getId());
        dto.setName(player.getName());
        dto.setCurrentPrice(player.getCurrentPrice());
        dto.setTokens(TokenJPADTO.desdeModelo(player.getTokens(), dto));
        dto.setExternalId(player.getExternalId());

        dto.setPlayerGameData(PlayerGameDataJPADTO.desdeModelo(player.getPlayerGameData(), dto));
        dto.setTeam(teamJPADTO);

        return dto;
    }

    public Player aModelo(Team team) {
        Player player = new Player();
        player.setId(this.id);
        player.setExternalId(this.getExternalId());
        player.setName(this.name);
        player.setCurrentPrice(this.currentPrice);
        player.setTokens(this.tokens.stream().map(token -> token.aModelo(player)).collect(Collectors.toCollection(ArrayList::new)));

        player.setPlayerGameData(this.playerGameData.aModelo(player));
        player.setTeam(team);
        return player;
    }

    public Player aModelo() {
        Player player = new Player();
        player.setId(this.id);
        player.setExternalId(this.getExternalId());
        player.setName(this.name);
        player.setCurrentPrice(this.currentPrice);
        player.setTokens(this.tokens.stream().map(token -> token.aModelo(player))
                .collect(Collectors.toCollection(ArrayList::new)));

        player.setPlayerGameData(this.playerGameData != null ? this.playerGameData.aModelo(player) : null);
        player.setTeam(this.team != null ? this.team.aModeloConJugadores() : null);
        return player;
    }
}
