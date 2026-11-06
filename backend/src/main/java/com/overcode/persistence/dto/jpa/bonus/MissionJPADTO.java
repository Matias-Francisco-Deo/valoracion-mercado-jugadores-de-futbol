package com.overcode.persistence.dto.jpa.bonus;

import com.overcode.model.bonus.mission.*;
import com.overcode.persistence.dto.jpa.PlayerJPADTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "mission")
@Table(name = "missions")
public class MissionJPADTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_mision", nullable = false)
    private String tipoMision;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private boolean completed = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "target_player_id")
    private PlayerJPADTO targetPlayer;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_mission_id")
    private List<MissionJPADTO> submissions = new ArrayList<>();

    public Mission<?> aModelo() {
        if ("VISIT_PLAYER".equalsIgnoreCase(this.tipoMision)) {
            return new VisitPlayerMission(
                    this.id,
                    this.nombre,
                    this.completed,
                    this.targetPlayer != null ? this.targetPlayer.aModeloSinTokens() : null
            );
        } else if ("BUY".equalsIgnoreCase(this.tipoMision)) {
            return new BuyMission(this.id, this.nombre, this.completed);
        } else if ("VISIT_3_PLAYERS".equalsIgnoreCase(this.tipoMision)) {
            List<Mission<?>> subs = new ArrayList<>();
            if (this.submissions != null) {
                for (MissionJPADTO sub : this.submissions) {
                    subs.add(sub.aModelo());
                }
            }
            return new Visit3PlayersMission(this.id, this.nombre, this.completed, subs);
        } else if ("DAILY_BONUS_OR".equalsIgnoreCase(this.tipoMision)) {
            List<Mission<?>> subs = new ArrayList<>();
            if (this.submissions != null) {
                for (MissionJPADTO sub : this.submissions) {
                    subs.add(sub.aModelo());
                }
            }
            return new DailyBonusOrMission(this.id, this.nombre, this.completed, subs);
        } else if ("OR".equalsIgnoreCase(this.tipoMision)) {
            List<Mission<?>> subs = new ArrayList<>();
            if (this.submissions != null) {
                for (MissionJPADTO sub : this.submissions) {
                    subs.add(sub.aModelo());
                }
            }
            return new GenericOrMission<>(this.id, this.nombre, this.completed, subs);
        } else if ("AND".equalsIgnoreCase(this.tipoMision)) {
            List<Mission<?>> subs = new ArrayList<>();
            if (this.submissions != null) {
                for (MissionJPADTO sub : this.submissions) {
                    subs.add(sub.aModelo());
                }
            }
            return new GenericAndMission<>(this.id, this.nombre, this.completed, subs);
        } else {
            return new TestMission<>(this.id, this.nombre, this.completed);
        }
    }

    public static MissionJPADTO desdeModelo(Mission<?> modelo) {
        if (modelo == null) {
            return null;
        }
        MissionJPADTO dto = new MissionJPADTO();
        dto.setId(modelo.getId());
        dto.setNombre(modelo.getNombre());
        dto.setCompleted(modelo.isCompleted());

        if (modelo instanceof VisitPlayerMission vpm) {
            dto.setTipoMision("VISIT_PLAYER");
            if (vpm.getTargetPlayer() != null && vpm.getTargetPlayer().getId() != null) {
                PlayerJPADTO playerDTO = new PlayerJPADTO();
                playerDTO.setId(vpm.getTargetPlayer().getId());
                playerDTO.setName(vpm.getTargetPlayer().getName());
                playerDTO.setCurrentPrice(vpm.getTargetPlayer().getCurrentPrice());
                dto.setTargetPlayer(playerDTO);
            }
        } else if (modelo instanceof BuyMission) {
            dto.setTipoMision("BUY");
        } else if (modelo instanceof Visit3PlayersMission) {
            dto.setTipoMision("VISIT_3_PLAYERS");
        } else if (modelo instanceof DailyBonusOrMission) {
            dto.setTipoMision("DAILY_BONUS_OR");
        } else if (modelo instanceof OrMission) {
            dto.setTipoMision("OR");
        } else if (modelo instanceof AndMission) {
            dto.setTipoMision("AND");
        } else {
            dto.setTipoMision("TEST");
        }

        if (modelo.getSubmissions() != null && !modelo.getSubmissions().isEmpty()) {
            List<MissionJPADTO> subs = new ArrayList<>();
            for (Mission<?> child : modelo.getSubmissions()) {
                subs.add(desdeModelo(child));
            }
            dto.setSubmissions(subs);
        }
        return dto;
    }
}
