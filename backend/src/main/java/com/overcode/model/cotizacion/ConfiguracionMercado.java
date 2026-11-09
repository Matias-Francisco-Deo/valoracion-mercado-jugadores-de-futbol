package com.overcode.model.cotizacion;

import com.overcode.model.bonus.DailyBonus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ConfiguracionMercado {
    private Long id;
    private EstrategiaCotizacion estrategiaCotizacion;
    private DailyBonus dailyBonus;

    public ConfiguracionMercado(EstrategiaCotizacion estrategiaCotizacion) {
        setEstrategiaCotizacion(estrategiaCotizacion);
    }

    public ConfiguracionMercado(Long id, EstrategiaCotizacion estrategiaCotizacion) {
        setId(id);
        setEstrategiaCotizacion(estrategiaCotizacion);
    }

    public ConfiguracionMercado(Long id, EstrategiaCotizacion estrategiaCotizacion, DailyBonus dailyBonus) {
        setId(id);
        setEstrategiaCotizacion(estrategiaCotizacion);
        setDailyBonus(dailyBonus);
    }

    public void cambiarEstrategiaDeCotizacionPor(EstrategiaCotizacion estrategiaCotizacion) {
        setEstrategiaCotizacion(estrategiaCotizacion);
    }

    public DailyBonus getLoginBonus() {
        return this.dailyBonus;
    }

    public void setLoginBonus(DailyBonus dailyBonus) {
        this.dailyBonus = dailyBonus;
    }
}
