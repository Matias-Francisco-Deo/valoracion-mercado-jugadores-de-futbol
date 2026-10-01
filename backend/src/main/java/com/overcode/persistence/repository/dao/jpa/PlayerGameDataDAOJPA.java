package com.overcode.persistence.repository.dao.jpa;

import com.overcode.persistence.dto.jpa.PlayerGameDataJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerGameDataDAOJPA extends JpaRepository<PlayerGameDataJPADTO, Long> {

    
}