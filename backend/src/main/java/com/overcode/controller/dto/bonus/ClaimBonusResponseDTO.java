package com.overcode.controller.dto.bonus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClaimBonusResponseDTO {
    private Double creditsAwarded;
    private Integer newStreak;
    private Double currentCreditBalance;
    private LocalDate claimDate;
    private String message;
}
