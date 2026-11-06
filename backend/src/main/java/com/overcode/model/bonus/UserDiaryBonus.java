package com.overcode.model.bonus;

import com.overcode.config.Reloj;
import com.overcode.model.bonus.mission.*;
import com.overcode.model.exception.bonus.DiaryBonusInvalidoException;
import com.overcode.model.exception.bonus.FechaReclamoBonusDiarioInvalidaException;
import com.overcode.model.player.Player;
import com.overcode.model.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.NonNull;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@NoArgsConstructor
public class UserDiaryBonus {

    private Long id;
    private User user;
    private Integer streakCount = 0;
    private LocalDate lastBonusClaimDate;
    private LocalDate activeMissionDate;
    private Mission<?> activeMission;

    public UserDiaryBonus(Long id, User user, Integer streakCount, LocalDate lastBonusClaimDate, LocalDate activeMissionDate, Mission<?> activeMission) {
        this.id = id;
        this.user = user;
        this.streakCount = streakCount != null ? streakCount : 0;
        this.lastBonusClaimDate = lastBonusClaimDate;
        this.activeMissionDate = activeMissionDate;
        this.activeMission = activeMission;
    }

    public UserDiaryBonus(User user) {
        this(null, user, 0, null, null, null);
    }

    @NonNull
    public LocalDate getFechaActual() {
        return Reloj.getLocalDate();
    }

    public boolean yaReclamoHoy(LocalDate hoy) {
        return lastBonusClaimDate != null && lastBonusClaimDate.isEqual(hoy);
    }

    public boolean yaReclamoHoy() {
        return yaReclamoHoy(getFechaActual());
    }

    public boolean puedeReclamarHoy() {
        return activeMission != null && activeMission.isCompleted() && !yaReclamoHoy(getFechaActual());
    }

    public void actualizarRacha() {
        if (lastBonusClaimDate == null) {
            return;
        }
        LocalDate hoy = getFechaActual();
        long dias = ChronoUnit.DAYS.between(lastBonusClaimDate, hoy);
        if (dias < 0) {
            throw new FechaReclamoBonusDiarioInvalidaException("La fecha actual no puede ser anterior a la fecha del último reclamo.");
        }
        if (dias > 1) {
            this.streakCount = 0;
        }
    }

    public Integer aumentarRacha() {
        actualizarRacha();
        if (this.streakCount == null || this.streakCount >= 6) {
            return 0;
        }
        return this.streakCount + 1;
    }

    public void inicializarConBonus(DiaryBonus diaryBonus) {
        LocalDate hoy = getFechaActual();
        if (this.activeMissionDate == null || !this.activeMissionDate.isEqual(hoy) || this.activeMission == null) {
            actualizarRacha();
            this.activeMissionDate = hoy;
            if (diaryBonus != null && diaryBonus.getRootMission() != null) {
                this.activeMission = diaryBonus.getRootMission().clonar();
            }
        }
    }

    public void registrarVisitaJugador(Player player) {
        if (player == null || this.activeMission == null) {
            return;
        }
        if (this.activeMission instanceof DailyBonusOrMission dom) {
            dom.completeVisit(player);
        } else if (this.activeMission instanceof Visit3PlayersMission v3pm) {
            v3pm.complete(player);
        } else if (this.activeMission instanceof VisitPlayerMission vpm) {
            vpm.complete(player);
        } else {
            dispatchVisitRecursively(this.activeMission, player);
        }
    }

    private void dispatchVisitRecursively(Mission<?> mission, Player player) {
        if (mission instanceof VisitPlayerMission vpm) {
            vpm.complete(player);
        } else if (mission instanceof Visit3PlayersMission v3pm) {
            v3pm.complete(player);
        } else {
            for (Mission<?> sub : mission.getSubmissions()) {
                dispatchVisitRecursively(sub, player);
            }
        }
    }

    public void registrarCompra() {
        if (this.activeMission == null) {
            return;
        }
        if (this.activeMission instanceof DailyBonusOrMission dom) {
            dom.complete((Void) null);
        } else if (this.activeMission instanceof BuyMission bm) {
            bm.complete();
        } else {
            dispatchBuyRecursively(this.activeMission);
        }
    }

    private void dispatchBuyRecursively(Mission<?> mission) {
        if (mission instanceof BuyMission bm) {
            bm.complete();
        } else {
            for (Mission<?> sub : mission.getSubmissions()) {
                dispatchBuyRecursively(sub);
            }
        }
    }

    public BonusClaim reclamarBonus(DiaryBonus diaryBonus) {
        if (diaryBonus == null) {
            throw new DiaryBonusInvalidoException("El DiaryBonus no puede ser nulo.");
        }
        diaryBonus.verificarPuedeReclamarBonus(this);
        actualizarRacha();
        Double bonusAmount = diaryBonus.getBonusConRacha(this.streakCount);
        Integer nuevaRacha = aumentarRacha();
        this.streakCount = nuevaRacha;
        this.lastBonusClaimDate = getFechaActual();
        if (this.user != null) {
            this.user.setCreditBalance(this.user.getCreditBalance() + bonusAmount);
            this.user.setStreakCount(nuevaRacha);
            this.user.setLastBonusClaimDate(this.lastBonusClaimDate);
        }
        return new BonusClaim(bonusAmount, nuevaRacha, this.user != null ? this.user.getCreditBalance() : 0.0);
    }
}
