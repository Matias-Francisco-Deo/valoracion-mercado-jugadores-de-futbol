package com.overcode.service.impl.bonus;

import com.overcode.model.bonus.BonusClaim;
import com.overcode.model.bonus.DailyBonus;
import com.overcode.model.bonus.EstadoBonus;
import com.overcode.model.bonus.UserDailyBonus;
import com.overcode.model.user.User;
import com.overcode.persistence.repository.interfaces.DailyBonusRepository;
import com.overcode.persistence.repository.interfaces.UserDailyBonusRepository;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.interfaces.ConfiguracionMercadoService;
import com.overcode.service.interfaces.DailyBonusService;
import com.overcode.service.interfaces.UserDailyBonusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DailyBonusServiceImpl implements DailyBonusService {

    private final UserDailyBonusService userDailyBonusService;
    private final ConfiguracionMercadoService configuracionMercadoService;
    private final DailyBonusRepository dailyBonusRepository;
    private final UserDailyBonusRepository userDailyBonusRepository;

    public DailyBonusServiceImpl(
                                 UserDailyBonusService userDailyBonusService,
                                 ConfiguracionMercadoService configuracionMercadoService,
                                 DailyBonusRepository dailyBonusRepository, UserDailyBonusRepository userDailyBonusRepository) {
        this.userDailyBonusService = userDailyBonusService;
        this.configuracionMercadoService = configuracionMercadoService;
        this.dailyBonusRepository = dailyBonusRepository;
        this.userDailyBonusRepository = userDailyBonusRepository;
    }

    @Override
    public BonusClaim reclamarBonus(User user) {
        UserDailyBonus userDailyBonus = userDailyBonusRepository.recuperarPorUserId(user.getId()).orElseThrow(
                () -> new EntidadNoEncontradaException("Bonus diario no encontrado para usuario: " + user.getId())
        );
        DailyBonus dailyBonus = recuperarSeleccionado();

        BonusClaim reclamoBonus = userDailyBonus.reclamarBonus(dailyBonus);

        userDailyBonusService.guardar(userDailyBonus);

        return reclamoBonus;
    }

    @Override
    public DailyBonus recuperarSeleccionado() {
        return configuracionMercadoService.recuperar().getDailyBonus();
    }

    @Override
    public DailyBonus guardar(DailyBonus dailyBonus) {
        return dailyBonusRepository.guardar(dailyBonus);
    }

    @Override
    public EstadoBonus obtenerEstadoBonus(User user) {
        UserDailyBonus userDailyBonus = userDailyBonusRepository.recuperarPorUserId(user.getId()).orElseThrow(
                () -> new EntidadNoEncontradaException("Bonus diario no encontrado para usuario: " + user.getId())
        );
        DailyBonus dailyBonus = recuperarSeleccionado();

        return dailyBonus.obtenerEstadoBonus(userDailyBonus); // TODO acá se actualizarían las misiones, ojo
    }
}
