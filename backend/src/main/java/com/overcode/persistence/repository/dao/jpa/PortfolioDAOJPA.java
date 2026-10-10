package com.overcode.persistence.repository.dao.jpa;

import com.overcode.persistence.dto.jpa.PortfolioJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PortfolioDAOJPA extends JpaRepository<PortfolioJPADTO, UUID> {
    Optional<PortfolioJPADTO> findByUserId(Long userId);
}
