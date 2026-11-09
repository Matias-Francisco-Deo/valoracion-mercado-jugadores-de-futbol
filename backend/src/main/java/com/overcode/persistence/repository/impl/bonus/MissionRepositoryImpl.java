package com.overcode.persistence.repository.impl.bonus;

import com.overcode.model.bonus.mission.DailyBonusMission;
import com.overcode.persistence.dto.jpa.bonus.MissionJPADTO;
import com.overcode.persistence.repository.dao.jpa.MissionDAOJPA;
import com.overcode.persistence.repository.interfaces.MissionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MissionRepositoryImpl implements MissionRepository {

    private final MissionDAOJPA missionDAOJPA;

    public MissionRepositoryImpl(MissionDAOJPA missionDAOJPA) {
        this.missionDAOJPA = missionDAOJPA;
    }

    @Override
    public DailyBonusMission<?> guardar(DailyBonusMission<?> dailyBonusMission) {
        MissionJPADTO dto = MissionJPADTO.desdeModelo(dailyBonusMission);
        return missionDAOJPA.save(dto).aModelo();
    }

    @Override
    public Optional<DailyBonusMission<?>> recuperarPorId(Long id) {
        return missionDAOJPA.findById(id).map(MissionJPADTO::aModelo);
    }

    @Override
    public void eliminarPorId(Long id) {
        missionDAOJPA.deleteById(id);
    }
}
