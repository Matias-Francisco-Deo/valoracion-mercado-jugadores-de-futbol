package com.overcode.persistence.repository.dao;

import com.overcode.persistence.dto.PlayerJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerDAOJPA extends JpaRepository<PlayerJPADTO, Long> {

    boolean existsByNameIgnoreCase(String name); // TODO cambiar esto?

    List<PlayerJPADTO> findAllByOrderByIdAsc(); // TODO cambiar esto?
}
