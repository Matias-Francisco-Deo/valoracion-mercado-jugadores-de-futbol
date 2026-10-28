package com.overcode.model.bonus;

import com.overcode.model.User;
import com.overcode.model.exception.bonus.BonusYaReclamadoHoyException;
import com.overcode.model.exception.bonus.FechaReclamoBonusLoginInvalidaException;
import com.overcode.model.exception.bonus.LoginBonusInvalidoException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.NonNull;

import java.time.LocalDate;
import java.time.ZoneOffset;

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

    public Double getBonusParaUser(User user) {
        Double bonus = getBonusConRacha(user.getStreakCount());
        verificarPuedeReclamarBonus(user);

        return bonus;
    }

    public EstadoBonus obtenerEstadoBonus(User user) {
        LocalDate fechaActual = getFechaActual();
        Integer rachaActual = user.getStreakCount();

        Double nextBonus = getBonusConRacha(rachaActual);

        return new EstadoBonus(rachaActual, nextBonus, user.yaReclamoBonus(fechaActual), this.bonusRewards);
    }


    public void verificarPuedeReclamarBonus(User user) {
        LocalDate fechaActual = getFechaActual();
        LocalDate ultimoReclamo = user.getLastBonusClaimDate();

        if (ultimoReclamo != null && fechaActual.isEqual(ultimoReclamo)) {
            throw new BonusYaReclamadoHoyException("El bonus diario de hoy ya ha sido reclamado.");
        }
        if (ultimoReclamo != null && fechaActual.isBefore(ultimoReclamo)) {
            throw new FechaReclamoBonusLoginInvalidaException("La fecha actual no puede ser anterior a la fecha del último reclamo.");
        }
    }

    @NonNull
    public LocalDate getFechaActual() {
        return LocalDate.now(ZoneOffset.UTC);
    }

    public LoginBonusRewards getBonusesDeLaSemana() {
        return this.bonusRewards;
    }
}
