package com.overcode.model.market;

import com.overcode.model.user.User;

public record PeticionDeposito(
        User user, // User?
        Double cantidad
) {

}
