package com.overcode.controller;

import com.overcode.controller.dto.bonus.ClaimBonusResponseDTO;
import com.overcode.controller.dto.bonus.DailyBonusStatusResponseDTO;
import com.overcode.service.interfaces.DailyBonusService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/daily-bonus")
@Tag(name = "Daily Bonus", description = "Endpoints for daily login streak bonus status and claims")
public class DailyBonusController {

    private final DailyBonusService dailyBonusService;

    public DailyBonusController(DailyBonusService dailyBonusService) {
        this.dailyBonusService = dailyBonusService;
    }

    @GetMapping
    public ResponseEntity<DailyBonusStatusResponseDTO> obtenerEstadoBonus(
            Principal principal) {
        DailyBonusStatusResponseDTO status = dailyBonusService.obtenerEstadoBonus(principal.getName());
        return ResponseEntity.ok(status);
    }

    @PostMapping("/claim")
    public ResponseEntity<ClaimBonusResponseDTO> reclamarBonus(
            Principal principal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaActual
    ) {
        ClaimBonusResponseDTO response = dailyBonusService.reclamarBonus(principal.getName());
        return ResponseEntity.ok(response);
    }
}
