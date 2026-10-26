package com.overcode.controller;

import com.overcode.controller.dto.bonus.ClaimBonusRequestDTO;
import com.overcode.controller.dto.bonus.ClaimBonusResponseDTO;
import com.overcode.controller.dto.bonus.DailyBonusRequestDTO;
import com.overcode.controller.dto.bonus.DailyBonusStatusResponseDTO;
import com.overcode.model.bonus.BonusClaim;
import com.overcode.model.bonus.EstadoBonus;
import com.overcode.service.interfaces.DailyBonusService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

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
        long id = Long.parseLong(principal.getName());

        DailyBonusRequestDTO dto = new DailyBonusRequestDTO(id);

        EstadoBonus status = dailyBonusService.obtenerEstadoBonus(dto.aModelo());
        return ResponseEntity.ok(DailyBonusStatusResponseDTO.desdeModelo(status));
    }

    @PostMapping("/claim")
    public ResponseEntity<ClaimBonusResponseDTO> reclamarBonus(
            Principal principal
    ) {
        long id = Long.parseLong(principal.getName());

        ClaimBonusRequestDTO dto = new ClaimBonusRequestDTO(id);

        BonusClaim claim = dailyBonusService.reclamarBonus(dto.aModelo());
        return ResponseEntity.ok(ClaimBonusResponseDTO.desdeModelo(claim));
    }
}
