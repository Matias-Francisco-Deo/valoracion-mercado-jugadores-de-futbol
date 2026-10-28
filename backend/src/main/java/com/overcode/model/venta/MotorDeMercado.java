package com.overcode.model.venta;

import com.overcode.model.user.User;
import com.overcode.service.exception.InsufficientLiquidityException;

import java.util.List;

public class MotorDeMercado {

    public TransaccionCompra comprarOrdenes(
            List<OrdenDeCompra> openOrders,
            Integer quantity,
            User buyer
    ) {
        if (openOrders.size() < quantity) {
            throw new InsufficientLiquidityException(
                    "Not enough liquidity to buy " + quantity + " tokens for player");
        }

        List<OrdenDeCompra> ordersToFill = openOrders.subList(0, quantity);

        return new TransaccionCompra(buyer.buyTokens(ordersToFill), ordersToFill);
    }
}
