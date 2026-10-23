package com.overcode.service.impl;

import com.overcode.model.User;
import com.overcode.model.bonus.BonusClaim;
import com.overcode.model.bonus.EstadoBonus;
import com.overcode.model.bonus.LoginBonus;
import com.overcode.model.cotizacion.ConfiguracionCotizaciones;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.exception.LoginBonusNoConfiguradoException;
import com.overcode.service.interfaces.ConfiguracionCotizacionesService;
import com.overcode.service.interfaces.DailyBonusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public BonusClaim reclamarBonus(String username) {
        User user = userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new EntidadNoEncontradaException("Usuario no encontrado: " + username));

        ConfiguracionCotizaciones config = configuracionCotizacionesService.recuperar();
        LoginBonus loginBonus = config.getLoginBonus();
        if (loginBonus == null) {
            throw new LoginBonusNoConfiguradoException("No hay ningún bonus de login activo configurado.");
        }

        BonusClaim reclamoBonus = user.reclamarBonusDiario(loginBonus);
        userRepository.guardar(user);

        return reclamoBonus;

//        return new ClaimBonusResponseDTO(
//                creditosOtorgados,
//                user.getStreakCount(),
//                user.getCreditBalance(),
//                user.getLastBonusClaimDate(),
//                "Bonus diario reclamado exitosamente."
//        );
    }

    @Override
    @Transactional(readOnly = true)
    public EstadoBonus obtenerEstadoBonus(String username) {
        User user = userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new EntidadNoEncontradaException("Usuario no encontrado: " + username));

        ConfiguracionCotizaciones config = configuracionCotizacionesService.recuperar(); // TODO ta feo
        LoginBonus loginBonus = config.getLoginBonus();
        if (loginBonus == null) {
            throw new LoginBonusNoConfiguradoException("No hay ningún bonus de login activo configurado.");
        }

        return loginBonus.obtenerEstadoBonus(user);
    }
}
