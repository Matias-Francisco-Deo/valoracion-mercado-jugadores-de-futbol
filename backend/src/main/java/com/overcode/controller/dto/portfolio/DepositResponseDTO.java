package com.overcode.controller.dto.portfolio;

import java.math.BigDecimal;

public class DepositResponseDTO {
    private final String message;
    private final BigDecimal credits;

    public DepositResponseDTO(String message, BigDecimal credits) {
        this.message = message;
        this.credits = credits;
    }

    public static DepositResponseDTO of(BigDecimal credits) {
        return new DepositResponseDTO("Credits deposited successfully", credits);
    }

    public String getMessage() { return message; }
    public BigDecimal getCredits() { return credits; }
}
