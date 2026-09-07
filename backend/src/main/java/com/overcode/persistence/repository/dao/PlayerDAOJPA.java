package com.overcode.persistence.repository.dao;

import com.overcode.persistence.dto.PlayerJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerDAOJPA extends JpaRepository<PlayerJPADTO, Long> {

}
