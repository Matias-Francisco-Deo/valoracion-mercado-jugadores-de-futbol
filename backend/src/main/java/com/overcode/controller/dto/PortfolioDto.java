package com.overcode.controller.dto;

import java.util.List;

public record PortfolioDto(Long userId, Integer creditBalance, List<PositionDto> positions) {
}
