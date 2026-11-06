package com.overcode.persistence.repository.dao.jpa;

import com.overcode.persistence.dto.jpa.bonus.DiaryBonusJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiaryBonusDAOJPA extends JpaRepository<DiaryBonusJPADTO, Long> {
    Optional<DiaryBonusJPADTO> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}
