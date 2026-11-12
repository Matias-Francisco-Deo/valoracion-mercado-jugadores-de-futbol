package com.overcode.model.bonus;

import com.overcode.config.Reloj;
import com.overcode.model.bonus.mission.DailyBonusMission;
import com.overcode.model.bonus.mission.MissionEngine;
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
        return activeDailyBonusMission.isCompleted() && !yaReclamoBonusHoy(getFechaActual());
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

        if (this.streakCount == 6) {
            setStreakCount(0);
            setLastBonusClaimDate(getFechaActual());
            return 0;
        }

        int newStreak = this.streakCount + 1;

        setStreakCount(newStreak);
        setLastBonusClaimDate(getFechaActual());

        return newStreak;
    }

    public void registrarVisitaJugador(Player player, DailyBonus dailyBonus) {

        actualizarMision(dailyBonus);

        if (this.activeDailyBonusMission == null) return;

        MissionEngine.registrarVisitaJugadorRecursivamente(this.activeDailyBonusMission, player);

    }

    public void registrarCompra(DailyBonus dailyBonus) { // TODO testear

        actualizarMision(dailyBonus);

        if (this.activeDailyBonusMission == null) return;

        MissionEngine.registrarCompraRecursivamente(this.activeDailyBonusMission);
    }


    public BonusClaim reclamarBonus(DailyBonus dailyBonus) {

        actualizarMision(dailyBonus); // TODO testear?

        dailyBonus.verificarPuedeReclamarBonus(this);

        actualizarRacha();

        Double bonusAmount = dailyBonus.getBonusConRacha(this.streakCount);

        aumentarRacha();

        user.addCredits(bonusAmount);

        return new BonusClaim(bonusAmount, getStreakCount(), user.getCreditBalance());
    }

    public void actualizarMision(DailyBonus dailyBonus) {
        // este method sirve para actualizar la misión apenas pase un día (o más), tomando la fecha de la última misión seteada
        // es importante que se use en cualquier method relacionado a interactuar con el bonus, para evitar problemas

        long dias = ChronoUnit.DAYS.between(getActiveMissionDate(), getFechaActual());

        boolean tieneMisionActualizada = this.getActiveMissionDate() != null && dias == 0;
        if (tieneMisionActualizada) return;

        dailyBonus.actualizarMisionEnUser(this);
    }
}
