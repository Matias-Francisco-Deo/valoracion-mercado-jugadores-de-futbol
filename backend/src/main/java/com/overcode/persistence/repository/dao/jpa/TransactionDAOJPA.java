package com.overcode.persistence.repository.dao.jpa;

import com.overcode.persistence.dto.jpa.TransactionJPADTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionDAOJPA extends JpaRepository<TransactionJPADTO, Long> {
    Page<TransactionJPADTO> findAllByOwner_IdOrderByDateDesc(Long userId, Pageable pageable
    );
}
