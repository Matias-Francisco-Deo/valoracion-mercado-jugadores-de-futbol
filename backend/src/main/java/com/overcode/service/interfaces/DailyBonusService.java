package com.overcode.service.interfaces;

import com.overcode.controller.dto.bonus.ClaimBonusResponseDTO;
import com.overcode.controller.dto.bonus.DailyBonusStatusResponseDTO;

import java.time.LocalDate;

public interface DailyBonusService {
    ClaimBonusResponseDTO reclamarBonus(String username, LocalDate fechaActual);
    DailyBonusStatusResponseDTO obtenerEstadoBonus(String username, LocalDate fechaActual);
}
