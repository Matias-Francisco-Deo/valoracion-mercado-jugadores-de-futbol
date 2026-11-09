package com.overcode.persistence.repository.dao.jpa;

import com.overcode.persistence.dto.jpa.bonus.DailyBonusJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DailyBonusDAOJPA extends JpaRepository<DailyBonusJPADTO, Long> {
    Optional<DailyBonusJPADTO> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}
