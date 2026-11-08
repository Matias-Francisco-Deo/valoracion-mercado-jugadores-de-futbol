package com.overcode.service.impl;

import com.overcode.model.bonus.DiaryBonus;
import com.overcode.model.cotizacion.ConfiguracionMercado;
import com.overcode.persistence.repository.interfaces.ConfiguracionMercadoRepository;
import com.overcode.persistence.repository.interfaces.DiaryBonusRepository;
import com.overcode.service.interfaces.DiaryBonusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DiaryBonusServiceImpl implements DiaryBonusService {

    private final ConfiguracionMercadoRepository configuracionMercadoRepository;
    private final DiaryBonusRepository diaryBonusRepository;

    public DiaryBonusServiceImpl(ConfiguracionMercadoRepository configuracionMercadoRepository, DiaryBonusRepository diaryBonusRepository) {
        this.configuracionMercadoRepository = configuracionMercadoRepository;
        this.diaryBonusRepository = diaryBonusRepository;
    }

    @Override
    public DiaryBonus recuperarSeleccionado() {
        return configuracionMercadoRepository.recuperarPorId(1L)
                .map(ConfiguracionMercado::getDiaryBonus)
                .orElse(null);
    }

    @Override
    public DiaryBonus guardar(DiaryBonus diaryBonus) {
        return diaryBonusRepository.guardar(diaryBonus);
    }
}
