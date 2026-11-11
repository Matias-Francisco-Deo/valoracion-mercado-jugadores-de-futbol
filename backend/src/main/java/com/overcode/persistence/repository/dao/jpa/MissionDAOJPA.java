package com.overcode.persistence.repository.dao.jpa;

import com.overcode.persistence.dto.jpa.bonus.mission.DailyBonusMissionJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionDAOJPA extends JpaRepository<DailyBonusMissionJPADTO, Long> {
}
