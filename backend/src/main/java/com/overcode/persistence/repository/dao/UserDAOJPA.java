package com.overcode.persistence.repository.dao;

import com.overcode.persistence.dto.UserJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDAOJPA extends JpaRepository<UserJPADTO, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<UserJPADTO> findByUsername(String username);

    Optional<UserJPADTO> findByEmail(String email);
}
