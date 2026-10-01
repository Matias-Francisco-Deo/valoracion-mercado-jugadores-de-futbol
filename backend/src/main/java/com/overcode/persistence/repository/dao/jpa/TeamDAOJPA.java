package com.overcode.persistence.repository.dao.jpa;

import com.overcode.persistence.dto.jpa.TeamJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamDAOJPA extends JpaRepository<TeamJPADTO, Long> {


}