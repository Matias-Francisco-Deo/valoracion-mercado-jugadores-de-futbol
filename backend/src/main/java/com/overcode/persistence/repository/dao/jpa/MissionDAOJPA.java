package com.overcode.persistence.repository.dao.jpa;

import com.overcode.persistence.dto.jpa.bonus.MissionJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionDAOJPA extends JpaRepository<MissionJPADTO, Long> {
}
