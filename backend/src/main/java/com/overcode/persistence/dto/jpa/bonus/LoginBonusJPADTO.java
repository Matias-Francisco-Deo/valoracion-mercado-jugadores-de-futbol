package com.overcode.persistence.dto.jpa.bonus;

import com.overcode.model.bonus.LoginBonus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "login_bonus")
@Table(name = "login_bonuses")
public class LoginBonusJPADTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(nullable = false)
    private LoginBonusRewardsJPADTO rewards;


    public LoginBonus aModelo() {
        return new LoginBonus(this.id, this.nombre, this.rewards.aModelo());
    }

    public static LoginBonusJPADTO desdeModelo(LoginBonus modelo) {
        if (modelo == null) {
            return null;
        }
        LoginBonusJPADTO dto = new LoginBonusJPADTO();
        dto.setId(modelo.getId());
        dto.setNombre(modelo.getNombre());
        dto.setRewards(LoginBonusRewardsJPADTO.desdeModelo(modelo.getBonusRewards()));
        return dto;
    }
}
