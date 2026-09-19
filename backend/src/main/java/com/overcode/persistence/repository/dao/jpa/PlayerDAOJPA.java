package com.overcode.persistence.repository.dao.jpa;

import com.overcode.persistence.dto.jpa.PlayerJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerDAOJPA extends JpaRepository<PlayerJPADTO, Long> {

    boolean existsByNameIgnoreCase(String name); // TODO cambiar esto?

    List<PlayerJPADTO> findAllByOrderByIdAsc(); // TODO cambiar esto?
}
