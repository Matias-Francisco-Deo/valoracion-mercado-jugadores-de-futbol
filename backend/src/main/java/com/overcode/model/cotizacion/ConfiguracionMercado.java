package com.overcode.model.cotizacion;

import com.overcode.model.bonus.DiaryBonus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ConfiguracionMercado {
    private Long id;
    private EstrategiaCotizacion estrategiaCotizacion;
    private DiaryBonus diaryBonus;

    public ConfiguracionMercado(EstrategiaCotizacion estrategiaCotizacion) {
        setEstrategiaCotizacion(estrategiaCotizacion);
    }

    public ConfiguracionMercado(Long id, EstrategiaCotizacion estrategiaCotizacion) {
        setId(id);
        setEstrategiaCotizacion(estrategiaCotizacion);
    }

    public ConfiguracionMercado(Long id, EstrategiaCotizacion estrategiaCotizacion, DiaryBonus diaryBonus) {
        setId(id);
        setEstrategiaCotizacion(estrategiaCotizacion);
        setDiaryBonus(diaryBonus);
    }

    public void cambiarEstrategiaDeCotizacionPor(EstrategiaCotizacion estrategiaCotizacion) {
        setEstrategiaCotizacion(estrategiaCotizacion);
    }

    public DiaryBonus getLoginBonus() {
        return this.diaryBonus;
    }

    public void setLoginBonus(DiaryBonus diaryBonus) {
        this.diaryBonus = diaryBonus;
    }
}
