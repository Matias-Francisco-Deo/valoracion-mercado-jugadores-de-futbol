package com.overcode.persistence.repository.dao.jpa;

import com.overcode.persistence.dto.jpa.AuditLogJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuditLogDAOJPA extends JpaRepository<AuditLogJPADTO, UUID> {
}
