package com.overcode.persistence.dto.jpa.bonus;

import com.overcode.model.bonus.mission.DailyBonusMission;
import com.overcode.persistence.dto.jpa.bonus.mapper.TipoDailyBonusMission;
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
@Entity(name = "mission")
@Table(name = "missions")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class DailyBonusMissionJPADTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(name = "tipo_mision", nullable = false) // TODO NO, buscar alternativa polimórfica, abstract class y todo eso
//    private String tipoMision;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private boolean completed;

//    @OneToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "target_player_id")
//    private PlayerJPADTO targetPlayer;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_mission_id")
    private List<DailyBonusMissionJPADTO> submissions = new ArrayList<>();

    public abstract DailyBonusMission<?> aModelo();

    public static DailyBonusMissionJPADTO desdeModelo(DailyBonusMission<?> mision) {
        DailyBonusMissionJPADTO dto = TipoDailyBonusMission.getValueOf(mision);
        dto.setId(mision.getId());
        dto.setCompleted(mision.getCompleted());
        dto.setNombre(mision.getNombre());
        dto.setSubmissions(mision.getSubmissions().stream().map(DailyBonusMissionJPADTO::desdeModelo).collect(Collectors.toList()));
        // ojo, nunca debería haber una misión que se llame a sí misma

        return dto;
    }

//    public DailyBonusMission<?> aModelo() { // TODO esto es básicamente un mapping, no está bien que se haga acá. Hay que manejar algo similar a las estrategias de cotización
//        if ("VISIT_PLAYER".equalsIgnoreCase(this.tipoMision)) {
//            return new VisitPlayerDailyBonusMission(
//                    this.id,
//                    this.nombre,
//                    this.completed,
//                    this.targetPlayer != null ? this.targetPlayer.aModeloSinTokens() : null
//            );
//        } else if ("BUY".equalsIgnoreCase(this.tipoMision)) {
//            return new BuyDailyBonusMission(this.id, this.nombre, this.completed);
//        } else if ("VISIT_3_PLAYERS".equalsIgnoreCase(this.tipoMision)) {
//            List<VisitPlayerDailyBonusMission> subs = new ArrayList<>();
//            if (this.submissions != null) {
//                for (DailyBonusMissionJPADTO sub : this.submissions) {
//                    subs.add((VisitPlayerDailyBonusMission) sub.aModelo());
//                }
//            }
//            return new Visit3PlayersDailyBonusMission(this.id, this.nombre, this.completed, subs);
//        } else if ("DAILY_BONUS_OR".equalsIgnoreCase(this.tipoMision)) {
//            List<DailyBonusMission<?>> subs = new ArrayList<>();
//            if (this.submissions != null) {
//                for (DailyBonusMissionJPADTO sub : this.submissions) {
//                    subs.add(sub.aModelo());
//                }
//            }
//            return new BuyOrVisitMission(this.id, this.nombre, this.completed, subs);
//        } else if ("OR".equalsIgnoreCase(this.tipoMision)) {
//            List<DailyBonusMission<?>> subs = new ArrayList<>();
//            if (this.submissions != null) {
//                for (DailyBonusMissionJPADTO sub : this.submissions) {
//                    subs.add(sub.aModelo());
//                }
//            }
//            return new GenericOrDailyBonusMission<>(this.id, this.nombre, this.completed, subs);
//        } else if ("AND".equalsIgnoreCase(this.tipoMision)) {
//            List<DailyBonusMission<?>> subs = new ArrayList<>();
//            if (this.submissions != null) {
//                for (DailyBonusMissionJPADTO sub : this.submissions) {
//                    subs.add(sub.aModelo());
//                }
//            }
//            return new GenericAndDailyBonusMission<>(this.id, this.nombre, this.completed, subs);
//        } else {
//            return new TestDailyBonusMission<>(this.id, this.nombre, this.completed);
//        }
//    }

//    public static DailyBonusMissionJPADTO desdeModelo(DailyBonusMission<?> modelo) {
//        if (modelo == null) {
//            return null;
//        }
//        DailyBonusMissionJPADTO dto = new DailyBonusMissionJPADTO();
//        dto.setId(modelo.getId());
//        dto.setNombre(modelo.getNombre());
//        dto.setCompleted(modelo.isCompleted());
//
//        if (modelo instanceof VisitPlayerDailyBonusMission vpm) { // TODO esto se va
//            dto.setTipoMision("VISIT_PLAYER");
//            if (vpm.getTargetPlayer() != null && vpm.getTargetPlayer().getId() != null) {
//                PlayerJPADTO playerDTO = new PlayerJPADTO();
//                playerDTO.setId(vpm.getTargetPlayer().getId());
//                playerDTO.setName(vpm.getTargetPlayer().getName());
//                playerDTO.setCurrentPrice(vpm.getTargetPlayer().getCurrentPrice());
//                dto.setTargetPlayer(playerDTO);
//            }
//        } else if (modelo instanceof BuyDailyBonusMission) {
//            dto.setTipoMision("BUY");
//        } else if (modelo instanceof Visit3PlayersDailyBonusMission) {
//            dto.setTipoMision("VISIT_3_PLAYERS");
//        } else if (modelo instanceof BuyOrVisitMission) {
//            dto.setTipoMision("DAILY_BONUS_OR");
//        } else if (modelo instanceof OrDailyBonusMission) {
//            dto.setTipoMision("OR");
//        } else if (modelo instanceof AndDailyBonusMission) {
//            dto.setTipoMision("AND");
//        } else {
//            dto.setTipoMision("TEST");
//        }
//
//        if (modelo.getSubmissions() != null && !modelo.getSubmissions().isEmpty()) {
//            List<DailyBonusMissionJPADTO> subs = new ArrayList<>();
//            for (DailyBonusMission<?> child : modelo.getSubmissions()) {
//                subs.add(desdeModelo(child));
//            }
//            dto.setSubmissions(subs);
//        }
//        return dto;
//    }
}
