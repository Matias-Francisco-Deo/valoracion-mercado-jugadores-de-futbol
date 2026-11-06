package com.overcode.model.bonus;

import com.overcode.model.bonus.mission.Mission;
import com.overcode.model.exception.bonus.BonusYaReclamadoHoyException;
import com.overcode.model.exception.bonus.DiaryBonusInvalidoException;
import com.overcode.model.exception.bonus.FechaReclamoBonusLoginInvalidaException;
import com.overcode.model.exception.bonus.RequisitosMisionesNoCumplidosException;
import com.overcode.model.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.NonNull;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static com.overcode.config.Reloj.getLocalDate;

@Getter
@Setter
@NoArgsConstructor
public class DiaryBonus {

    private Long id;
    private String nombre;
    private DiaryBonusRewards bonusRewards;
    private Mission<?> rootMission;

    public DiaryBonus(Long id, String nombre, DiaryBonusRewards bonusRewards, Mission<?> rootMission) {
        this.id = id;
        setNombre(nombre);
        setBonusRewards(bonusRewards);
        this.rootMission = rootMission;
    }

    public DiaryBonus(String nombre, DiaryBonusRewards bonusRewards, Mission<?> rootMission) {
        this(null, nombre, bonusRewards, rootMission);
    }

    public DiaryBonus(Long id, String nombre, DiaryBonusRewards bonusRewards) {
        this(id, nombre, bonusRewards, null);
    }

    public DiaryBonus(String nombre, DiaryBonusRewards bonusRewards) {
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

    public Double getBonusParaUsuario(UserDiaryBonus userBonus) {
        Double bonus = getBonusConRacha(userBonus.getStreakCount());
        verificarPuedeReclamarBonus(userBonus);
        return bonus;
    }

    public Double getBonusParaUser(User user) {
        Double bonus = getBonusConRacha(user.getStreakCount());
        verificarPuedeReclamarBonus(user);
        return bonus;
    }

    public EstadoBonus obtenerEstadoBonus(UserDiaryBonus userBonus) {
        LocalDate fechaActual = getFechaActual();
        Integer rachaActual = userBonus.getStreakCount();
        Double nextBonus = getBonusConRacha(rachaActual);
        boolean yaReclamo = userBonus.yaReclamoHoy(fechaActual);
        boolean puedeReclamar = userBonus.puedeReclamarHoy();
        List<Mission<?>> misiones = userBonus.getActiveMission() != null
                ? (userBonus.getActiveMission().getSubmissions().isEmpty()
                    ? List.of(userBonus.getActiveMission())
                    : userBonus.getActiveMission().getSubmissions())
                : Collections.emptyList();

        return new EstadoBonus(rachaActual, nextBonus, yaReclamo, this.bonusRewards, puedeReclamar, misiones);
    }

    public EstadoBonus obtenerEstadoBonus(User user) {
        LocalDate fechaActual = getFechaActual();
        Integer rachaActual = user.getStreakCount();
        Double nextBonus = getBonusConRacha(rachaActual);

        return new EstadoBonus(rachaActual, nextBonus, user.yaReclamoBonus(fechaActual), this.bonusRewards);
    }

    public void verificarPuedeReclamarBonus(UserDiaryBonus userBonus) {
        if (userBonus == null) {
            throw new DiaryBonusInvalidoException("El UserDiaryBonus no puede ser nulo.");
        }
        LocalDate fechaActual = getFechaActual();
        LocalDate ultimoReclamo = userBonus.getLastBonusClaimDate();

        if (ultimoReclamo != null && fechaActual.isEqual(ultimoReclamo)) {
            throw new BonusYaReclamadoHoyException("El bonus diario de hoy ya ha sido reclamado.");
        }
        if (ultimoReclamo != null && fechaActual.isBefore(ultimoReclamo)) {
            throw new FechaReclamoBonusLoginInvalidaException("La fecha actual no puede ser anterior a la fecha del último reclamo.");
        }
        if (!userBonus.puedeReclamarHoy()) {
            throw new RequisitosMisionesNoCumplidosException("No se han cumplido los requisitos de las misiones para reclamar el bonus.");
        }
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
        return getLocalDate();
    }

    public DiaryBonusRewards getBonusesDeLaSemana() {
        return this.bonusRewards;
    }
}
