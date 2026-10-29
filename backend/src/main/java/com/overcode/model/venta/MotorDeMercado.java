package com.overcode.model.venta;

import com.overcode.model.exception.TokensInsuficientesParaComprarException;
import com.overcode.model.player.Player;
import com.overcode.model.token.Token;
import com.overcode.model.user.User;
import com.overcode.model.venta.transaction.TransaccionFinanciera;
import com.overcode.model.venta.transaction.TransactionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MotorDeMercado {

    public TransaccionCompra comprarOrdenes(
            List<OrdenDeCompra> openOrders,
            Integer quantity,
            User buyer
    ) {
        if (openOrders.size() < quantity) {
            throw new TokensInsuficientesParaComprarException(
                    "No hay suficientes órdenes para comprar " + quantity + " tokens del jugador");
        }

        List<OrdenDeCompra> ordersToFill = openOrders.subList(0, quantity);

        Map<Long, User> sellersById = new HashMap<>();

        for (OrdenDeCompra order : ordersToFill) {
            User seller = order.getOwner();

            User existingSeller = sellersById.putIfAbsent(seller.getId(), seller);

            if (existingSeller != null) {order.setOwner(existingSeller);}

            order.executePurchase(buyer);
        }

        //crear un objeto aparte para crear las transacciones?
        List<TransaccionFinanciera> transaccionFinancieras = crearTransacciones(buyer, ordersToFill);

        return new TransaccionCompra(transaccionFinancieras, ordersToFill);
    }

    private List<TransaccionFinanciera> crearTransacciones(User buyer, List<OrdenDeCompra> ordersToFill) {
        List<TransaccionFinanciera> transaccionFinancieras = new ArrayList<>();

        Player player = ordersToFill.get(0).getPlayer();

        transaccionFinancieras.add(new TransaccionFinanciera(buyer, player, TransactionType.BUY, ordersToFill.size()));

        Map<User, Integer> quantityBySeller = ordersToFill.stream()
                .collect(Collectors.groupingBy(
                        OrdenDeCompra::getOwner,
                        Collectors.summingInt(order -> 1)
                ));

        quantityBySeller.forEach((seller, quantity1) ->
                transaccionFinancieras.add(new TransaccionFinanciera(seller, player, TransactionType.SELL, quantity1))
        );
        return transaccionFinancieras;
    }

    public List<OrdenDeCompra> venderTokens(User seller, Player player, int quantity) {
        List<Token> userTokens =  seller.releaseTokensForSale(player, quantity);
        List<Token> tokensToSell = player.getTokensFrom(userTokens);

        return tokensToSell.stream().map(token -> this.venderToken(token, player, seller)).toList();
    }

    private OrdenDeCompra venderToken(Token token, Player player, User seller) {
        token.ponerALaVenta();
        return new OrdenDeCompra(
                seller,
                player,
                token,
                player.getCurrentPrice());
    }

    public List<OrdenDeCompra> publicarJugador(Player player, User superuser) {
        List<Token> tokens = player.getTokens(); // se lo damos al superuser
        superuser.addTokens(tokens);
        return venderTokens(superuser, player, tokens.size());
    }
}
