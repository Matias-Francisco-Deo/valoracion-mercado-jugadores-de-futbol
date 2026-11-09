package com.overcode.service.impl.bonus;

import com.overcode.model.bonus.DailyBonus;
import com.overcode.persistence.repository.interfaces.DailyBonusRepository;
import com.overcode.service.interfaces.ConfiguracionMercadoService;
import com.overcode.service.interfaces.DiaryBonusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DiaryBonusServiceImpl implements DiaryBonusService {

    private final ConfiguracionMercadoService configuracionMercadoService;
    private final DailyBonusRepository dailyBonusRepository;

    public DiaryBonusServiceImpl(ConfiguracionMercadoService configuracionMercadoService, DailyBonusRepository dailyBonusRepository) {
        this.configuracionMercadoService = configuracionMercadoService;
        this.dailyBonusRepository = dailyBonusRepository;
    }

    @Override
    public DailyBonus recuperarSeleccionado() {
        return configuracionMercadoService.recuperar().getDailyBonus();
    }

    @Override
    public DailyBonus guardar(DailyBonus dailyBonus) {
        return dailyBonusRepository.guardar(dailyBonus);
    }
}
