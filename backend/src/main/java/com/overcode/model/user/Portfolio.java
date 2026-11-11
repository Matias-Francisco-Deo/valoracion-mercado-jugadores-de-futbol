package com.overcode.model.user;

import com.overcode.model.market.Position;
import com.overcode.model.market.transaction.TransaccionFinanciera;
import com.overcode.model.player.Player;
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

}


