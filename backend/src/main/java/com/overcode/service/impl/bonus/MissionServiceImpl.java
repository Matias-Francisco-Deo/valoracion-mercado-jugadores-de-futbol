package com.overcode.service.impl.bonus;

import com.overcode.model.bonus.mission.Mission;
import com.overcode.persistence.repository.interfaces.MissionRepository;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.interfaces.MissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MissionServiceImpl implements MissionService {

    private final MissionRepository missionRepository;

    public MissionServiceImpl(MissionRepository missionRepository) {
        this.missionRepository = missionRepository;
    }

    @Override
    public Mission<?> guardar(Mission<?> mission) {
        return missionRepository.guardar(mission);
    }

    @Override
    public Mission<?> recuperarPorId(Long id) {
        return missionRepository.recuperarPorId(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Misión no encontrada con id: " + id));
    }
}
