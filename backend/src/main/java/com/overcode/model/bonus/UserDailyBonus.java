package com.overcode.model.bonus;

import com.overcode.config.Reloj;
import com.overcode.model.bonus.mission.*;
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
public class UserDailyBonus {

    private Long id;
    private User user;
    private Integer streakCount = 0;
    private LocalDate lastBonusClaimDate;
    private LocalDate activeMissionDate;
    private DailyBonusMission<?> activeDailyBonusMission;

    public UserDailyBonus(Long id, User user, Integer streakCount, LocalDate lastBonusClaimDate, LocalDate activeMissionDate, DailyBonusMission<?> activeDailyBonusMission) {
        this.id = id;
        this.user = user;
        this.streakCount = streakCount;
        this.lastBonusClaimDate = lastBonusClaimDate;
        this.activeMissionDate = activeMissionDate;
        this.activeDailyBonusMission = activeDailyBonusMission;
    }

    public UserDailyBonus(User user) {
        this(null, user, 0, null, null, null);
    }

    @NonNull
    public LocalDate getFechaActual() {
        return Reloj.getLocalDate();
    }

    public boolean yaReclamoBonusHoy(LocalDate hoy) {
        return lastBonusClaimDate != null && lastBonusClaimDate.isEqual(hoy);
    }

    public boolean puedeReclamarBonusHoy() {
        return activeDailyBonusMission != null && activeDailyBonusMission.isCompleted() && !yaReclamoBonusHoy(getFechaActual());
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
        int newStreak = this.streakCount + 1;

        setStreakCount(newStreak);
        setLastBonusClaimDate(getFechaActual());

        return newStreak;
    }

    public void inicializarConBonus(DailyBonus dailyBonus) {
        LocalDate hoy = getFechaActual();

        // TODO si la misión es de hoy, no la cambio, si es de ayer u otro día pasado, sí la reemplazo
        if (this.activeMissionDate == null || !this.activeMissionDate.isEqual(hoy) || this.activeDailyBonusMission == null) {
            actualizarRacha();
            this.activeMissionDate = hoy;
            if (dailyBonus != null && dailyBonus.getRootDailyBonusMission() != null) {
                this.activeDailyBonusMission = dailyBonus.getRootDailyBonusMission().clonar(); // TODO más que nada debería hacer esto
            }
        }
    }

    public void registrarVisitaJugador(Player player) { // TODO embellecer, ta feo
        if (player == null || this.activeDailyBonusMission == null) {
            return;
        }
        if (this.activeDailyBonusMission instanceof BuyOrVisitMission dom) {
            dom.completeVisit(player);
        } else if (this.activeDailyBonusMission instanceof Visit3PlayersDailyBonusMission v3pm) {
            v3pm.complete(player);
        } else if (this.activeDailyBonusMission instanceof VisitPlayerDailyBonusMission vpm) {
            vpm.complete(player);
        } else {
            dispatchVisitRecursively(this.activeDailyBonusMission, player);
        }
    }

    private void dispatchVisitRecursively(DailyBonusMission<?> dailyBonusMission, Player player) {
        if (dailyBonusMission instanceof VisitPlayerDailyBonusMission vpm) {
            vpm.complete(player);
        } else if (dailyBonusMission instanceof Visit3PlayersDailyBonusMission v3pm) {
            v3pm.complete(player);
        } else {
            for (DailyBonusMission<?> sub : dailyBonusMission.getSubmissions()) {
                dispatchVisitRecursively(sub, player);
            }
        }
    }

    public void registrarCompra() {
        if (this.activeDailyBonusMission == null) {
            return;
        }
        if (this.activeDailyBonusMission instanceof BuyOrVisitMission dom) { // TODO no seas así, esto está claramente mal hermano, ya de por sí es un OR XD
            dom.complete((Void) null);
        } else if (this.activeDailyBonusMission instanceof BuyDailyBonusMission bm) { // esta sí
            bm.complete();
        } else {
            dispatchBuyRecursively(this.activeDailyBonusMission); // TODO generalizar esto? por ahí pasando el mismo switch...
        }
    }

    private void dispatchBuyRecursively(DailyBonusMission<?> dailyBonusMission) {
        if (dailyBonusMission instanceof BuyDailyBonusMission bm) {
            bm.complete();
        } else {
            for (DailyBonusMission<?> sub : dailyBonusMission.getSubmissions()) {
                dispatchBuyRecursively(sub);
            }
        }
    }

    public BonusClaim reclamarBonus(DailyBonus dailyBonus) {
        dailyBonus.verificarPuedeReclamarBonus(this);

        actualizarRacha();

        Double bonusAmount = dailyBonus.getBonusConRacha(this.streakCount);

        aumentarRacha();

        user.addCredits(bonusAmount);

        return new BonusClaim(bonusAmount, getStreakCount(), user.getCreditBalance());
    }
}
