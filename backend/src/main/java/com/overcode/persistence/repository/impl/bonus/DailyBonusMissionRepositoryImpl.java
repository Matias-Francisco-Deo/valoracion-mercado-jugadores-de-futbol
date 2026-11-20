package com.overcode.persistence.repository.impl.bonus;

import com.overcode.model.bonus.mission.DailyBonusMission;
import com.overcode.persistence.dto.jpa.bonus.mission.DailyBonusMissionJPADTO;
import com.overcode.persistence.repository.dao.jpa.DailyBonusMissionDAOJPA;
import com.overcode.persistence.repository.interfaces.DailyBonusMissionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class DailyBonusMissionRepositoryImpl implements DailyBonusMissionRepository {

    private final DailyBonusMissionDAOJPA dailyBonusMissionDAOJPA;

    public DailyBonusMissionRepositoryImpl(DailyBonusMissionDAOJPA dailyBonusMissionDAOJPA) {
        this.dailyBonusMissionDAOJPA = dailyBonusMissionDAOJPA;
    }

    @Override
    public DailyBonusMission<?> guardar(DailyBonusMission<?> dailyBonusMission) {
        DailyBonusMissionJPADTO dto = DailyBonusMissionJPADTO.desdeModelo(dailyBonusMission);
        return dailyBonusMissionDAOJPA.save(dto).aModelo();
    }

    @Override
    public Optional<DailyBonusMission<?>> recuperarPorId(Long id) {
        return dailyBonusMissionDAOJPA.findById(id).map(DailyBonusMissionJPADTO::aModelo);
    }

    @Override
    public void eliminarPorId(Long id) {
        dailyBonusMissionDAOJPA.deleteById(id);
    }
}
