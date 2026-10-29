package com.overcode.model.venta;

import com.overcode.model.exception.InsufficientLiquidityException;
import com.overcode.model.player.Player;
import com.overcode.model.token.Token;
import com.overcode.model.user.User;

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
