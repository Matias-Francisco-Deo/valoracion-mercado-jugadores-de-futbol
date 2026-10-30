package com.overcode.controller;

import com.overcode.controller.dto.bonus.ClaimBonusRequestDTO;
import com.overcode.controller.dto.bonus.ClaimBonusResponseDTO;
import com.overcode.controller.dto.bonus.DailyBonusRequestDTO;
import com.overcode.controller.dto.bonus.DailyBonusStatusResponseDTO;
import com.overcode.model.bonus.BonusClaim;
import com.overcode.model.bonus.EstadoBonus;
import com.overcode.service.interfaces.BonusService;
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

    private final BonusService bonusService;

    public DailyBonusController(BonusService bonusService) {
        this.bonusService = bonusService;
    }

    @GetMapping
    public ResponseEntity<DailyBonusStatusResponseDTO> obtenerEstadoBonus(
            Principal principal) {
        long id = Long.parseLong(principal.getName());

        DailyBonusRequestDTO dto = new DailyBonusRequestDTO(id);

        EstadoBonus status = bonusService.obtenerEstadoBonus(dto.aModelo());
        return ResponseEntity.ok(DailyBonusStatusResponseDTO.desdeModelo(status));
    }

    @PostMapping("/claim")
    public ResponseEntity<ClaimBonusResponseDTO> reclamarBonus(
            Principal principal
    ) {
        long id = Long.parseLong(principal.getName());

        ClaimBonusRequestDTO dto = new ClaimBonusRequestDTO(id);

        BonusClaim claim = bonusService.reclamarBonus(dto.aModelo());
        return ResponseEntity.ok(ClaimBonusResponseDTO.desdeModelo(claim));
    }
}
