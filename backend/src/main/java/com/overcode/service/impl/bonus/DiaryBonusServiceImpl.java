package com.overcode.service.impl.bonus;

import com.overcode.model.bonus.DiaryBonus;
import com.overcode.persistence.repository.interfaces.DiaryBonusRepository;
import com.overcode.service.interfaces.ConfiguracionMercadoService;
import com.overcode.service.interfaces.DiaryBonusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DiaryBonusServiceImpl implements DiaryBonusService {

    private final ConfiguracionMercadoService configuracionMercadoService;
    private final DiaryBonusRepository diaryBonusRepository;

    public DiaryBonusServiceImpl(ConfiguracionMercadoService configuracionMercadoService, DiaryBonusRepository diaryBonusRepository) {
        this.configuracionMercadoService = configuracionMercadoService;
        this.diaryBonusRepository = diaryBonusRepository;
    }

    @Override
    public DiaryBonus recuperarSeleccionado() {
        return configuracionMercadoService.recuperar().getDiaryBonus();
    }

    @Override
    public DiaryBonus guardar(DiaryBonus diaryBonus) {
        return diaryBonusRepository.guardar(diaryBonus);
    }
}
