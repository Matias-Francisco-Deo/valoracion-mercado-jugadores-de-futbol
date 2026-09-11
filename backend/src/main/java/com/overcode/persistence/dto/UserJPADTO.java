package com.overcode.persistence.dto;

import com.overcode.model.Token;
import com.overcode.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Entity(name="user")
@Table(name = "users")
public class UserJPADTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "credit_balance", nullable = false)
    private Integer creditBalance;

    @OrderBy("id ASC")
    @Column(name = "tokens")
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TokenJPADTO> tokens = new ArrayList<>();

    public UserJPADTO(Long id, String username, String email, String password, Integer creditBalance,  List<TokenJPADTO> tokens) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.creditBalance = creditBalance;
        this.tokens = tokens;
    }

    public static UserJPADTO desdeModelo(User user) {
        if (user == null) {
            return null;
        }
        UserJPADTO dto = new UserJPADTO();

        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setCreditBalance(user.getCreditBalance());
        dto.setTokens(TokenJPADTO.desdeModelo(user.getTokens(), dto));
        return dto;
    }

    public User aModelo() {

        User user = new User();
        user.setId(this.id);
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setCreditBalance(this.creditBalance);
        user.setTokens(this.tokens.stream().map(token -> token.aModelo(user)).collect(Collectors.toList()));
        return user;
    }

}
