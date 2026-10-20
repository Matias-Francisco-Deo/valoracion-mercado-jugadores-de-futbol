package com.overcode.model;

import com.overcode.model.transaction.Transaction;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class Position {

    private Player player;

    private Integer quantity;
    private Double averagePurchasePrice;
    private Double currentPrice;
    private Double currentValue;
    private Double profitLoss;
    private Double profitLossPercentage;

    public Position(Player player, List<Transaction> transactions) {
        setPlayer(player);
        setCurrentPrice(player.getCurrentPrice().doubleValue());

        calcularPositions(transactions);
    }


    private void calcularPositions(List<Transaction> transactions) {
        int tokensEnPosesion = 0;
        double costoAcumulado = 0.0; // Costo de adquisición de los tokens que quedan

        for (Transaction transaccion : transactions) {
            int tokensDeTransaccion = transaccion.getQuantity();

            switch (transaccion.getType()) {
                case BUY -> {
                    tokensEnPosesion += tokensDeTransaccion;
                    costoAcumulado += transaccion.getTotalPrice();
                }

                case SELL -> {
                    if (tokensDeTransaccion > tokensEnPosesion) {
                        throw new IllegalStateException("La venta supera la cantidad de tokens disponibles");
                    }

                    double costoPromedioAnterior = tokensEnPosesion > 0 ? costoAcumulado / tokensEnPosesion : 0.0; // Costo promedio por token antes de la venta

                    tokensEnPosesion -= tokensDeTransaccion;
                    costoAcumulado = costoPromedioAnterior * tokensEnPosesion;// Conservamos el costo correspondiente a los tokens restantes
                }
            }
        }

        double precioPromedioCalculado = tokensEnPosesion > 0 ? costoAcumulado / tokensEnPosesion : 0.0;

        double valorActualCalculado = tokensEnPosesion * getCurrentPrice();

        double rendimientoCalculado = valorActualCalculado - costoAcumulado;// cantidad de ganancia o pérdida no realizada

        double rendimientoPorcentualCalculado = costoAcumulado > 0 ? (rendimientoCalculado / costoAcumulado) * 100 : 0.0; // Ganancia o pérdida como porcentaje del costo restante

        setQuantity(tokensEnPosesion);
        setAveragePurchasePrice(precioPromedioCalculado);
        setCurrentValue(valorActualCalculado);
        setProfitLoss(rendimientoCalculado);
        setProfitLossPercentage(rendimientoPorcentualCalculado);
    }

}

