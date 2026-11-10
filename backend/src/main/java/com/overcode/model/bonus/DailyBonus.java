package com.overcode.model.bonus;

import com.overcode.model.bonus.mission.DailyBonusMission;
import com.overcode.model.exception.bonus.BonusYaReclamadoHoyException;
import com.overcode.model.exception.bonus.DailyBonusInvalidoException;
import com.overcode.model.exception.bonus.FechaReclamoBonusLoginInvalidaException;
import com.overcode.model.exception.bonus.RequisitosMisionesNoCumplidosException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.NonNull;

import java.time.LocalDate;

import static com.overcode.config.Reloj.getLocalDate;

@Getter
@Setter
@NoArgsConstructor
public class DailyBonus {

    private Long id;
    private String nombre;
    private DailyBonusRewards bonusRewards;
    private DailyBonusMission<?> rootDailyBonusMission;

    public DailyBonus(Long id, String nombre, DailyBonusRewards bonusRewards, DailyBonusMission<?> rootDailyBonusMission) {
        this.id = id;
        setNombre(nombre);
        setBonusRewards(bonusRewards);
        this.rootDailyBonusMission = rootDailyBonusMission;
    }

    public DailyBonus(String nombre, DailyBonusRewards bonusRewards, DailyBonusMission<?> rootDailyBonusMission) {
        this(null, nombre, bonusRewards, rootDailyBonusMission);
    }

    public DailyBonus(Long id, String nombre, DailyBonusRewards bonusRewards) {
        this(id, nombre, bonusRewards, null);
    }

    public DailyBonus(String nombre, DailyBonusRewards bonusRewards) {
        this(null, nombre, bonusRewards, null);
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new DailyBonusInvalidoException("El nombre del DiaryBonus no puede ser nulo ni vacío.");
        }
        this.nombre = nombre.trim();
    }

    public Double getBonusConRacha(Integer racha) {
        if (this.bonusRewards == null) {
            throw new DailyBonusInvalidoException("Las recompensas no están configuradas.");
        }
        return this.bonusRewards.get(racha);
    }

    public Double getBonusParaUser(UserDailyBonus userDailyBonus) {
        Double bonus = getBonusConRacha(userDailyBonus.getStreakCount());
        verificarPuedeReclamarBonus(userDailyBonus);
        return bonus;
    }

    public EstadoBonus obtenerEstadoBonus(UserDailyBonus userBonus) {
        LocalDate fechaActual = getFechaActual();
        Integer rachaActual = userBonus.getStreakCount();
        Double nextBonus = getBonusConRacha(rachaActual);
        boolean yaReclamo = userBonus.yaReclamoBonusHoy(fechaActual);
        boolean puedeReclamar = userBonus.puedeReclamarBonusHoy();
        DailyBonusMission<?> mision = userBonus.getActiveDailyBonusMission();
        return new EstadoBonus(rachaActual, nextBonus, yaReclamo, this.bonusRewards, puedeReclamar, mision);
    }

    public void verificarPuedeReclamarBonus(UserDailyBonus userBonus) {
        LocalDate fechaActual = getFechaActual();
        LocalDate ultimoReclamo = userBonus.getLastBonusClaimDate();

        if (ultimoReclamo != null && fechaActual.isEqual(ultimoReclamo)) {
            throw new BonusYaReclamadoHoyException("El bonus diario de hoy ya ha sido reclamado.");
        }
        if (ultimoReclamo != null && fechaActual.isBefore(ultimoReclamo)) {
            throw new FechaReclamoBonusLoginInvalidaException("La fecha actual no puede ser anterior a la fecha del último reclamo.");
        }
        if (!userBonus.puedeReclamarBonusHoy()) { // TODO testear esto
            throw new RequisitosMisionesNoCumplidosException("No se han cumplido los requisitos de las misiones para reclamar el bonus.");
        }
    }

    @NonNull
    public LocalDate getFechaActual() {
        return getLocalDate();
    }

    public DailyBonusRewards getBonusesDeLaSemana() {
        return this.bonusRewards;
    }
}
