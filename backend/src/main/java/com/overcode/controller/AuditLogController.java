package com.overcode.controller;

import com.overcode.controller.dto.audit.AuditLogResponseDTO;
import com.overcode.service.interfaces.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/audits")
@Tag(name = "Audit Log", description = "Admin-only view of token emission and trade operations")
public class AuditLogController {

    private final AuditService auditService;

    public AuditLogController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    @Operation(summary = "List audit log", description = "Returns every EMISSION, BUY and SELL operation recorded.")
    public ResponseEntity<List<AuditLogResponseDTO>> listar() {
        List<AuditLogResponseDTO> body = auditService.listarTodo().stream()
                .map(AuditLogResponseDTO::desdeModelo)
                .toList();
        return ResponseEntity.ok(body);
    }
}
