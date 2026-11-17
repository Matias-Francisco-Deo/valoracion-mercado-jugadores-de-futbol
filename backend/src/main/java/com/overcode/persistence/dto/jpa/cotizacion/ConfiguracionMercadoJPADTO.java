package com.overcode.persistence.dto.jpa.cotizacion;

import com.overcode.model.cotizacion.ConfiguracionMercado;
import com.overcode.persistence.dto.jpa.bonus.DailyBonusJPADTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "configuracion_mercado")
@Table(name = "configuraciones_mercado")
@Setter
@Getter
@NoArgsConstructor
public class ConfiguracionMercadoJPADTO {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @NotNull
    private EstrategiaCotizacionJPADTO estrategiaCotizacion;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "daily_bonus_id")
    private DailyBonusJPADTO dailyBonus;

    public ConfiguracionMercado aModelo() {
        ConfiguracionMercado configuracion = new ConfiguracionMercado();
        configuracion.setId(getId());
        if (getEstrategiaCotizacion() != null) {
            configuracion.setEstrategiaCotizacion(getEstrategiaCotizacion().aModelo());
        }
        if (getDailyBonus() != null) {
            configuracion.setDailyBonus(getDailyBonus().aModelo());
        }
        return configuracion;
    }

    public static ConfiguracionMercadoJPADTO desdeModelo(ConfiguracionMercado configuracion) {
        if (configuracion == null) {
            return null;
        }
        ConfiguracionMercadoJPADTO dto = new ConfiguracionMercadoJPADTO();
        dto.setId(configuracion.getId());
        if (configuracion.getEstrategiaCotizacion() != null) {
            dto.setEstrategiaCotizacion(EstrategiaCotizacionJPADTO.desdeModelo(configuracion.getEstrategiaCotizacion()));
        }
        if (configuracion.getDailyBonus() != null) {
            dto.setDailyBonus(DailyBonusJPADTO.desdeModelo(configuracion.getDailyBonus()));
        }
        return dto;
    }
}
