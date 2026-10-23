package com.overcode.model.bonus;

import com.overcode.model.User;
import com.overcode.model.bonus.exception.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.NonNull;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class LoginBonus {

    private Long id;
    private String nombre;
    private Map<DiaDeLaSemana, Double> bonusRewards;

    public LoginBonus(Long id, String nombre, Map<DiaDeLaSemana, Double> bonusRewards) {
        this.id = id;
        setNombre(nombre);
        setBonusRewards(bonusRewards);
    }

    public LoginBonus(String nombre, Map<DiaDeLaSemana, Double> bonusRewards) {
        this(null, nombre, bonusRewards);
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new LoginBonusInvalidoException("El nombre del LoginBonus no puede ser nulo ni vacío.");
        }
        this.nombre = nombre.trim();
    }

    public void setBonusRewards(Map<DiaDeLaSemana, Double> bonusRewards) {
        if (bonusRewards == null || bonusRewards.size() != DiaDeLaSemana.values().length) {
            throw new LoginBonusInvalidoException("El mapa de bonusRewards debe contener los 7 días de la semana.");
        }
        for (DiaDeLaSemana dia : DiaDeLaSemana.values()) {
            if (!bonusRewards.containsKey(dia)) {
                throw new LoginBonusInvalidoException("Falta configurar el bonus para el día: " + dia);
            }
            Double valor = bonusRewards.get(dia);
            if (valor == null || valor <= 0.0) {
                throw new ValorBonusInvalidoException("El valor de bonus para " + dia + " debe ser mayor a 0.0.");
            }
        }
        this.bonusRewards = new EnumMap<>(bonusRewards);
    }

    public Double getBonusParaDia(DiaDeLaSemana dia) {
        if (dia == null) {
            throw new DiaDeLaSemanaInvalidoException("El día de la semana no puede ser nulo.");
        }
        return this.bonusRewards.get(dia);
    }

    public BonusClaim getBonusParaUser(User user) {
        Integer racha = calcularRacha(user);
        Double bonus = getBonusParaDia(DiaDeLaSemana.desdeNumeroDia(racha));

        user.addCredits(bonus);
        user.setStreakCount(racha);
        user.setLastBonusClaimDate(getFechaActual());

        return new BonusClaim(bonus, racha);
    }

    public EstadoBonus obtenerEstadoBonus(User user) {
        LocalDate fechaActual = getFechaActual();
        Integer rachaActual = user.getStreakCount();

        Integer siguienteRacha = calcularRachaSiguiente(user);
        Double todayBonus = getBonusParaDia(DiaDeLaSemana.desdeNumeroDia(siguienteRacha));

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

    public Map<DiaDeLaSemana, Double> getBonusesDeLaSemana() {
        return Collections.unmodifiableMap(this.bonusRewards);
    }
}
