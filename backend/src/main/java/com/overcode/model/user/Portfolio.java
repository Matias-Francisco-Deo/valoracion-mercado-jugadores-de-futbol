package com.overcode.model.user;

import com.overcode.model.player.Player;
import com.overcode.model.venta.Position;
import com.overcode.model.venta.transaction.TransaccionFinanciera;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class Portfolio {
    private List<Position> positions;

    public Portfolio(List<Position> positions) {
        setPositions(positions);
    }

    public static Portfolio desdeTransacciones(List<TransaccionFinanciera> transaccionFinancieras) {
        Map<Long, List<TransaccionFinanciera>> transactionsPorJugador = new LinkedHashMap<>();

        for (TransaccionFinanciera transaccionFinanciera : transaccionFinancieras) {
            Long playerId = transaccionFinanciera.getPlayer().getId();
            // las agrupa por id de jugador o si no está crea una lista para sumarla al hashmap
            transactionsPorJugador.computeIfAbsent(playerId, id -> new ArrayList<>()).add(transaccionFinanciera);
        }

        List<Position> positions = new ArrayList<>();

        for (Map.Entry<Long, List<TransaccionFinanciera>> entry : transactionsPorJugador.entrySet()) {
            List<TransaccionFinanciera> transactionsDelJugador = entry.getValue();

            Player player = transactionsDelJugador.get(0).getPlayer();

            Position position = new Position(player, transactionsDelJugador);
            if (position.getQuantity() > 0) {
                positions.add(position);
            }
        }
        return new Portfolio(positions);
    }
}


