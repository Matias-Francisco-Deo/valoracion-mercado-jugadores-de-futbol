package com.overcode.persistency.repository;

import com.overcode.persistency.dto.UserRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserRecord, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<UserRecord> findByUsername(String username);
}
