package com.overcode.persistence.repository.dao;

import com.overcode.persistence.dto.PlayerJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerDAO extends JpaRepository<PlayerJPADTO, Long> {

    Optional<PlayerJPADTO> findByName(String name);
}
