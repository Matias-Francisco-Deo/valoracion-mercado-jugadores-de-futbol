package com.overcode.persistence.repository.impl;

import com.overcode.model.bonus.mission.Mission;
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
    public Mission<?> guardar(Mission<?> mission) {
        MissionJPADTO dto = MissionJPADTO.desdeModelo(mission);
        return missionDAOJPA.save(dto).aModelo();
    }

    @Override
    public Optional<Mission<?>> recuperarPorId(Long id) {
        return missionDAOJPA.findById(id).map(MissionJPADTO::aModelo);
    }

    @Override
    public void eliminarPorId(Long id) {
        missionDAOJPA.deleteById(id);
    }
}
