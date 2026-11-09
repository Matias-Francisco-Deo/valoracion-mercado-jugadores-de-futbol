package com.overcode.persistence.dto.jpa;

import com.overcode.model.user.User;
import com.overcode.persistence.dto.jpa.bonus.UserDailyBonusJPADTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
    private Double creditBalance;

//    @Column(name = "streak_count", nullable = false)
//    private Integer streakCount = 0;
//
//    @Column(name = "last_bonus_claim_date")
//    private LocalDate lastBonusClaimDate;

    @OrderBy("id ASC")
    @Column(name = "tokens")
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TokenJPADTO> tokens = new ArrayList<>();

    @JoinColumn(name = "daily_bonus", nullable = false)
    @OneToOne(fetch = FetchType.EAGER)
    private UserDailyBonusJPADTO userDailyBonusJPADTO;

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
        dto.setUserDailyBonusJPADTO(UserDailyBonusJPADTO.desdeModelo(user.getUserDailyBonus()));
//        dto.setStreakCount(user.getStreakCount() != null ? user.getStreakCount() : 0);
//        dto.setLastBonusClaimDate(user.getLastBonusClaimDate());
        dto.setTokens(TokenJPADTO.desdeModelo(user.getTokens(), dto));
        return dto;
    }

    public static UserJPADTO desdeModeloSinTokens(User user) {
        if (user == null) {
            return null;
        }
        UserJPADTO dto = new UserJPADTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setCreditBalance(user.getCreditBalance());
        dto.setUserDailyBonusJPADTO(UserDailyBonusJPADTO.desdeModelo(user.getUserDailyBonus()));
//        dto.setStreakCount(user.getStreakCount() != null ? user.getStreakCount() : 0);
//        dto.setLastBonusClaimDate(user.getLastBonusClaimDate());
        return dto;
    }

    public User aModelo() {

        User user = new User();
        user.setId(this.id);
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setCreditBalance(this.creditBalance);
        user.setUserDailyBonus(this.userDailyBonusJPADTO.aModelo());
//        user.setStreakCount(this.streakCount != null ? this.streakCount : 0);
//        user.setLastBonusClaimDate(this.lastBonusClaimDate);
        user.setTokens(new ArrayList<>(this.tokens.stream().map(token -> token.aModelo(user)).toList()));
        return user;
    }

    public User aModeloSinTokens() {
        User user = new User();
        user.setId(this.id);
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setCreditBalance(this.creditBalance);
        user.setUserDailyBonus(this.userDailyBonusJPADTO.aModelo());
//        user.setStreakCount(this.streakCount != null ? this.streakCount : 0);
//        user.setLastBonusClaimDate(this.lastBonusClaimDate);
        user.setTokens(new ArrayList<>());
        return user;
    }

}
