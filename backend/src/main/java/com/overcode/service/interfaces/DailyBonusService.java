package com.overcode.service.interfaces;

import com.overcode.controller.dto.bonus.ClaimBonusResponseDTO;

public interface DailyBonusService {
    ClaimBonusResponseDTO reclamarBonus(String username);
    EstadoBonus obtenerEstadoBonus(String username);
}
