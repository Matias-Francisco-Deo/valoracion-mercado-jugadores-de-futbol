package com.overcode.persistency.repository;

import com.overcode.persistency.dto.PlayerRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<PlayerRecord, Long> {

    Optional<PlayerRecord> findByName(String name);
}
