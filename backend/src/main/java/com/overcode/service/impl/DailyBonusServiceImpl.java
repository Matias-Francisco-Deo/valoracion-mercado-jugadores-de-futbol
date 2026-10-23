package com.overcode.service.impl;

import com.overcode.controller.dto.bonus.ClaimBonusResponseDTO;
import com.overcode.controller.dto.bonus.DailyBonusStatusResponseDTO;
import com.overcode.model.User;
import com.overcode.model.bonus.LoginBonus;
import com.overcode.model.cotizacion.ConfiguracionCotizaciones;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.exception.LoginBonusNoConfiguradoException;
import com.overcode.service.interfaces.ConfiguracionCotizacionesService;
import com.overcode.service.interfaces.DailyBonusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class DailyBonusServiceImpl implements DailyBonusService {

    private final UserRepository userRepository;
    private final ConfiguracionCotizacionesService configuracionCotizacionesService;

    public DailyBonusServiceImpl(UserRepository userRepository,
                                 ConfiguracionCotizacionesService configuracionCotizacionesService) {
        this.userRepository = userRepository;
        this.configuracionCotizacionesService = configuracionCotizacionesService;
    }

    @Override
    public ClaimBonusResponseDTO reclamarBonus(String username, LocalDate fechaActual) {
        User user = userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new EntidadNoEncontradaException("Usuario no encontrado: " + username));

        ConfiguracionCotizaciones config = configuracionCotizacionesService.recuperar();
        LoginBonus loginBonus = config.getLoginBonus();
        if (loginBonus == null) {
            throw new LoginBonusNoConfiguradoException("No hay ningún bonus de login activo configurado.");
        }

        Double creditosOtorgados = user.reclamarBonusDiario(fechaActual, loginBonus);
        userRepository.guardar(user);

        return new ClaimBonusResponseDTO(
                creditosOtorgados,
                user.getStreakCount(),
                user.getCreditBalance(),
                user.getLastBonusClaimDate(),
                "Bonus diario reclamado exitosamente."
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DailyBonusStatusResponseDTO obtenerEstadoBonus(String username, LocalDate fechaActual) {
        User user = userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new EntidadNoEncontradaException("Usuario no encontrado: " + username));

        ConfiguracionCotizaciones config = configuracionCotizacionesService.recuperar();
        LoginBonus loginBonus = config.getLoginBonus();
        if (loginBonus == null) {
            throw new LoginBonusNoConfiguradoException("No hay ningún bonus de login activo configurado.");
        }

        boolean puedeReclamar = user.puedeReclamarBonusDiarioHoy(fechaActual, loginBonus);
        boolean yaReclamado = !puedeReclamar;

        int rachaActual = user.getStreakCount();
        Double todayBonus;
        if (yaReclamado) {
            todayBonus = rachaActual > 0 ? loginBonus.getBonusParaRacha(rachaActual) : loginBonus.getBonusParaRacha(1);
        } else {
            int siguienteRacha = loginBonus.calcularSiguienteRacha(user.getLastBonusClaimDate(), fechaActual, rachaActual);
            todayBonus = loginBonus.getBonusParaRacha(siguienteRacha);
        }

        return new DailyBonusStatusResponseDTO(
                rachaActual,
                todayBonus,
                yaReclamado,
                loginBonus.getBonusesDeLaSemana()
        );
    }
}
