package com.overcode.persistence.dto.jpa.bonus;

import com.overcode.model.bonus.DiaDeLaSemana;
import com.overcode.model.bonus.LoginBonus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "login_bonus_rewards", joinColumns = @JoinColumn(name = "login_bonus_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "dia_semana")
    @Column(name = "creditos", nullable = false)
    private Map<DiaDeLaSemana, Double> rewards;

    public LoginBonusJPADTO(Long id, String nombre, Map<DiaDeLaSemana, Double> rewards) {
        this.id = id;
        this.nombre = nombre;
        this.rewards = rewards;
    }

    public LoginBonus aModelo() {
        return new LoginBonus(this.id, this.nombre, this.rewards);
    }

    public static LoginBonusJPADTO desdeModelo(LoginBonus modelo) {
        if (modelo == null) {
            return null;
        }
        LoginBonusJPADTO dto = new LoginBonusJPADTO();
        dto.setId(modelo.getId());
        dto.setNombre(modelo.getNombre());
        dto.setRewards(modelo.getBonusRewards());
        return dto;
    }
}
