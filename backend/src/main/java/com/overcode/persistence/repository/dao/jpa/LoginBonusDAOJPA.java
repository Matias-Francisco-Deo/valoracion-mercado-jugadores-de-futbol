package com.overcode.persistence.repository.dao.jpa;

import com.overcode.persistence.dto.jpa.bonus.LoginBonusJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginBonusDAOJPA extends JpaRepository<LoginBonusJPADTO, Long> {
    Optional<LoginBonusJPADTO> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}
