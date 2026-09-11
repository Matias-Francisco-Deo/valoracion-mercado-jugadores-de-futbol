package com.overcode.persistence.dto;

import com.overcode.model.Player;
import com.overcode.model.Token;
import com.overcode.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Entity(name="token")
@Table(name = "tokens")
@Setter
@Getter
@NoArgsConstructor
public class TokenJPADTO {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private UserJPADTO owner;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private PlayerJPADTO player;

    public TokenJPADTO(Long id, UserJPADTO userJPADTO, PlayerJPADTO playerJPADTO) {
        setId(id);
        setOwner(userJPADTO);
        setPlayer(playerJPADTO);
    }

    public static TokenJPADTO desdeModelo(Token token) {
        if (token == null) {
            return null;
        }
        return new TokenJPADTO(
            token.getId(),
            UserJPADTO.desdeModelo(token.getOwner()),
            PlayerJPADTO.desdeModelo(token.getPlayer())
        );
    }

    public static TokenJPADTO desdeModelo(Token token, PlayerJPADTO player) {
        if (token == null) {
            return null;
        }
        return new TokenJPADTO(
                token.getId(),
                UserJPADTO.desdeModelo(token.getOwner()),
                player
        );
    }

    public static TokenJPADTO desdeModelo(Token token, UserJPADTO user) {
        if (token == null) {
            return null;
        }
        return new TokenJPADTO(
                token.getId(),
                user,
                PlayerJPADTO.desdeModelo(token.getPlayer())
        );
    }

    public static List<TokenJPADTO> desdeModelo(List<Token> tokens, PlayerJPADTO player) {
        return tokens.stream().map(token -> TokenJPADTO.desdeModelo(token, player)).collect(Collectors.toList());
    }

    public static List<TokenJPADTO> desdeModelo(List<Token> tokens, UserJPADTO user) {
        return tokens.stream().map(token -> TokenJPADTO.desdeModelo(token, user)).collect(Collectors.toList());
    }

    public Token aModelo(User user) {
        Token token = new Token();
        token.setId(getId());
        token.setOwner(user);
        token.setPlayer(getPlayer().aModelo());
        return token;
    }

    public Token aModelo(Player player) {
        Token token = new Token();
        token.setId(getId());
        token.setOwner(getOwner() == null ? null : getOwner().aModelo());
        token.setPlayer(player);
        return token;
    }


}
