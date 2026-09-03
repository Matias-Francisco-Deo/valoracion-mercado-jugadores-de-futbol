package com.overcode.persistency.repository;

import com.overcode.persistency.dto.PositionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PositionRepository extends JpaRepository<PositionRecord, Long> {

    Optional<PositionRecord> findByUserIdAndPlayerId(Long userId, Long playerId);

    List<PositionRecord> findByUserId(Long userId);

    List<PositionRecord> findByPlayerId(Long playerId);
}
