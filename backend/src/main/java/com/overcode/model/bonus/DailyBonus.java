package com.overcode.model.bonus;

import com.overcode.model.bonus.mission.Mission;
import com.overcode.model.exception.bonus.BonusYaReclamadoHoyException;
import com.overcode.model.exception.bonus.DiaryBonusInvalidoException;
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
    private Mission<?> rootMission;

    public DailyBonus(Long id, String nombre, DailyBonusRewards bonusRewards, Mission<?> rootMission) {
        this.id = id;
        setNombre(nombre);
        setBonusRewards(bonusRewards);
        this.rootMission = rootMission;
    }

    public DailyBonus(String nombre, DailyBonusRewards bonusRewards, Mission<?> rootMission) {
        this(null, nombre, bonusRewards, rootMission);
    }

    public DailyBonus(Long id, String nombre, DailyBonusRewards bonusRewards) {
        this(id, nombre, bonusRewards, null);
    }

    public DailyBonus(String nombre, DailyBonusRewards bonusRewards) {
        this(null, nombre, bonusRewards, null);
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new DiaryBonusInvalidoException("El nombre del DiaryBonus no puede ser nulo ni vacío.");
        }
        this.nombre = nombre.trim();
    }

    public Double getBonusConRacha(Integer racha) {
        if (this.bonusRewards == null) {
            throw new DiaryBonusInvalidoException("Las recompensas no están configuradas.");
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
        Mission<?> mision = userBonus.getActiveMission();
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
