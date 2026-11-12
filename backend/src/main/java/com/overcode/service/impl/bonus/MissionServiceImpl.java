package com.overcode.service.impl.bonus;

import com.overcode.model.bonus.mission.DailyBonusMission;
import com.overcode.persistence.repository.interfaces.DailyBonusMissionRepository;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.interfaces.MissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MissionServiceImpl implements MissionService {

    private final DailyBonusMissionRepository dailyBonusMissionRepository;

    public MissionServiceImpl(DailyBonusMissionRepository dailyBonusMissionRepository) {
        this.dailyBonusMissionRepository = dailyBonusMissionRepository;
    }

    @Override
    public DailyBonusMission<?> guardar(DailyBonusMission<?> dailyBonusMission) {
        return dailyBonusMissionRepository.guardar(dailyBonusMission);
    }

    @Override
    public DailyBonusMission<?> recuperarPorId(Long id) {
        return dailyBonusMissionRepository.recuperarPorId(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Misión no encontrada con id: " + id));
    }
}
