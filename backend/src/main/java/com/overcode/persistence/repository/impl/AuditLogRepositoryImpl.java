package com.overcode.persistence.repository.impl;

import com.overcode.model.AuditLog;
import com.overcode.persistence.dto.jpa.AuditLogJPADTO;
import com.overcode.persistence.repository.dao.jpa.AuditLogDAOJPA;
import com.overcode.persistence.repository.interfaces.AuditLogRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class AuditLogRepositoryImpl implements AuditLogRepository {

    private final AuditLogDAOJPA auditLogDAOJPA;

    public AuditLogRepositoryImpl(AuditLogDAOJPA auditLogDAOJPA) {
        this.auditLogDAOJPA = auditLogDAOJPA;
    }

    @Override
    public AuditLog save(AuditLog auditLog) {
        AuditLogJPADTO dto = AuditLogJPADTO.desdeModelo(auditLog);
        AuditLogJPADTO saved = auditLogDAOJPA.save(dto);
        return saved.aModelo();
    }

    @Override
    public List<AuditLog> findAll() {
        return auditLogDAOJPA.findAll().stream()
                .map(AuditLogJPADTO::aModelo)
                .collect(Collectors.toList());
    }
}
