package com.overcode.persistence.repository.dao.jpa;

import com.overcode.persistence.dto.jpa.TeamJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamDAOJPA extends JpaRepository<TeamJPADTO, Long> {
    Optional<TeamJPADTO> findByName(String name);
    Optional<TeamJPADTO> findByNameAndLeague(String name, String league);
}