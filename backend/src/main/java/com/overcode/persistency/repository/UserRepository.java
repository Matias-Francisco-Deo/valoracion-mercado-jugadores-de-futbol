package com.overcode.persistency.repository;

import com.overcode.persistency.dto.UserJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserJPADTO, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<UserJPADTO> findByUsername(String username);
}
