package com.overcode.controller.dto.user;

import com.overcode.model.user.User;

public record DepositResponseDTO(String message, Double credits) {

    public static DepositResponseDTO desdeModelo(User user) {
        return new DepositResponseDTO("¡Créditos depositados con éxito!", user.getCreditBalance());
    }

}
