package com.overcode.controller.dto.user;

import com.overcode.persistence.dto.jpa.SummaryTokenPlayerDTO;
import org.springframework.data.domain.Page;

public record PortfolioDataDTO(//respuesta del service
        Page<SummaryTokenPlayerDTO> availableTokens,
        Page<SummaryTokenPlayerDTO> sellingTokens
) {
}
