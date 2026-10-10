package com.overcode.service.impl;

import com.overcode.model.AuditLog;
import com.overcode.persistence.repository.interfaces.AuditLogRepository;
import com.overcode.service.interfaces.AuditService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public List<AuditLog> listarTodo() {
        return auditLogRepository.findAll();
    }
}
