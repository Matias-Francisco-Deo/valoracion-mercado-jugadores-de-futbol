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
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
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

    public static List<TokenJPADTO> desdeModelo(List<Token> tokens) {
        return tokens.stream().map(TokenJPADTO::desdeModelo).collect(Collectors.toList());
    }

    public Token aModelo() {
        Token token = new Token();
        token.setId(getId());
        token.setOwner(getOwner().aModelo());
        token.setPlayer(getPlayer().aModelo());
        return token;
    }


}
