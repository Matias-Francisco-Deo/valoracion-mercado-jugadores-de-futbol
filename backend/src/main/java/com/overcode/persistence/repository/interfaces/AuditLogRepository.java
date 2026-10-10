package com.overcode.persistence.repository.interfaces;

import com.overcode.model.AuditLog;
import java.util.List;

public interface AuditLogRepository {
    AuditLog save(AuditLog auditLog);
    List<AuditLog> findAll();
}
