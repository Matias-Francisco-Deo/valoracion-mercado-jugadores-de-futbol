package com.overcode.model.market;

import com.overcode.model.exception.OrdenesInsuficientesParaCancelarException;
import com.overcode.model.exception.TokensInsuficientesParaComprarException;
import com.overcode.model.market.orden.OrdenDeVenta;
import com.overcode.model.market.transaction.TransaccionFinanciera;
import com.overcode.model.market.transaction.TransactionType;
import com.overcode.model.player.Player;
import com.overcode.model.token.Token;
import com.overcode.model.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MotorDeMercado {

    public TransaccionCompra comprarOrdenes(
            List<OrdenDeVenta> openOrders,
            Integer quantity,
            User buyer
    ) {
        if (openOrders.size() < quantity) {
            throw new TokensInsuficientesParaComprarException(
                    "No hay suficientes órdenes para comprar " + quantity + " tokens del jugador");
        }

        List<OrdenDeVenta> ordersToFill = openOrders.subList(0, quantity);

        Map<Long, User> sellersById = new HashMap<>();

        for (OrdenDeVenta order : ordersToFill) {
            User seller = order.getOwner();

            User existingSeller = sellersById.putIfAbsent(seller.getId(), seller);

            if (existingSeller != null) {order.setOwner(existingSeller);}

            order.comprarPorUsuario(buyer);
        }

        //crear un objeto aparte para crear las transacciones?
        List<TransaccionFinanciera> transaccionFinancieras = crearTransacciones(buyer, ordersToFill);

        return new TransaccionCompra(transaccionFinancieras, ordersToFill);
    }

    public void cancelarOrdenes(List<OrdenDeVenta> openOrders,User user,Integer quantity){
        if(openOrders.size()<quantity){
            throw new OrdenesInsuficientesParaCancelarException("No hay suficientes órdenes para cancelar " + quantity + " tokens del jugador");
        }

        for (int i = 0; i < quantity; i++) {
            OrdenDeVenta order = openOrders.get(i);
            order.cancel(user);
            user.addToken(order.getToken());
        }
    }

    private List<TransaccionFinanciera> crearTransacciones(User buyer, List<OrdenDeVenta> ordersToFill) {
        List<TransaccionFinanciera> transaccionFinancieras = new ArrayList<>();

        Player player = ordersToFill.get(0).getPlayer();

        transaccionFinancieras.add(new TransaccionFinanciera(buyer, player, TransactionType.BUY, ordersToFill.size()));

        Map<User, Integer> quantityBySeller = ordersToFill.stream()
                .collect(Collectors.groupingBy(
                        OrdenDeVenta::getOwner,
                        Collectors.summingInt(order -> 1)
                ));

        quantityBySeller.forEach((seller, quantity1) ->
                transaccionFinancieras.add(new TransaccionFinanciera(seller, player, TransactionType.SELL, quantity1))
        );
        return transaccionFinancieras;
    }

    public List<OrdenDeVenta> venderTokens(User seller, Player player, int quantity) {
        List<Token> userTokens =  seller.releaseTokensForSale(player, quantity);
        List<Token> tokensToSell = player.getTokensFrom(userTokens);

        return tokensToSell.stream().map(token -> this.venderToken(token, player, seller)).toList();
    }

    private OrdenDeVenta venderToken(Token token, Player player, User seller) {
        token.ponerALaVenta();
        return new OrdenDeVenta(
                seller,
                player,
                token,
                player.getCurrentPrice());
    }

    public List<OrdenDeVenta> publicarJugador(Player player, User superuser) {
        List<Token> tokens = player.getTokens(); // se lo damos al superuser
        superuser.addTokens(tokens);
        return venderTokens(superuser, player, tokens.size());
    }
}
