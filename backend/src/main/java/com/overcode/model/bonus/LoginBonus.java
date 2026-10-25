package com.overcode.model.bonus;

import com.overcode.model.User;
import com.overcode.model.bonus.exception.BonusYaReclamadoHoyException;
import com.overcode.model.bonus.exception.FechaReclamoInvalidaException;
import com.overcode.model.bonus.exception.LoginBonusInvalidoException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.NonNull;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@NoArgsConstructor
public class LoginBonus {

    private Long id;
    private String nombre;
    private LoginBonusRewards bonusRewards;

    public LoginBonus(Long id, String nombre, LoginBonusRewards bonusRewards) {
        this.id = id;
        setNombre(nombre);
        setBonusRewards(bonusRewards);
    }

    public LoginBonus(String nombre, LoginBonusRewards bonusRewards) {
        this(null, nombre, bonusRewards);
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new LoginBonusInvalidoException("El nombre del LoginBonus no puede ser nulo ni vacío.");
        }
        this.nombre = nombre.trim();
    }

    public Double getBonusConRacha(Integer racha) {
        return this.bonusRewards.get(racha);
    }

    public BonusClaim getBonusParaUser(User user) {
        Double bonus = getBonusConRacha(user.getStreakCount());
        Integer racha = calcularRacha(user);

        user.addCredits(bonus);
        user.setStreakCount(racha);
        user.setLastBonusClaimDate(getFechaActual());

        return new BonusClaim(bonus, racha);
    }

    public EstadoBonus obtenerEstadoBonus(User user) {
        LocalDate fechaActual = getFechaActual();
        Integer rachaActual = user.getStreakCount();

        Integer siguienteRacha = calcularRachaSiguiente(user);
        Double todayBonus = getBonusConRacha(siguienteRacha);

        return new EstadoBonus(rachaActual, todayBonus, user.yaReclamoBonus(fechaActual), this.bonusRewards);
    }

    public Integer calcularRachaSiguiente(User user) {
        Integer rachaActual = user.getStreakCount();
        if (rachaActual == null || rachaActual >= 7) {
            return 1;
        }
        return rachaActual + 1;
    }

    public Integer calcularRacha(User user) {
        LocalDate fechaActual = getFechaActual();
        LocalDate ultimoReclamo = user.getLastBonusClaimDate();
        Integer rachaActual = user.getStreakCount();

        if (ultimoReclamo == null) {
            return 1;
        }
        if (fechaActual.isEqual(ultimoReclamo)) {
            throw new BonusYaReclamadoHoyException("El bonus diario de hoy ya ha sido reclamado.");
        }
        if (fechaActual.isBefore(ultimoReclamo)) {
            throw new FechaReclamoInvalidaException("La fecha actual no puede ser anterior a la fecha del último reclamo.");
        }

        long diasEntre = ChronoUnit.DAYS.between(ultimoReclamo, fechaActual);
        if (diasEntre == 1) {
            if (rachaActual >= 7) {
                return 1;
            }
            return rachaActual + 1;
        }
        return 1;
    }

//    public boolean puedeReclamarHoy(LocalDate ultimoReclamo, LocalDate fechaActual) {
//        return ultimoReclamo == null || fechaActual.isAfter(ultimoReclamo);
//    }

    @NonNull
    public LocalDate getFechaActual() {
        return LocalDate.now(ZoneOffset.UTC);
    }

    public LoginBonusRewards getBonusesDeLaSemana() {
        return this.bonusRewards;
    }
}
