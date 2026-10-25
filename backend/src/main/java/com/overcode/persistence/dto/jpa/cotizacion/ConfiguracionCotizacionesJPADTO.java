package com.overcode.persistence.dto.jpa.cotizacion;

import com.overcode.model.cotizacion.ConfiguracionCotizaciones;
import com.overcode.persistence.dto.jpa.bonus.LoginBonusJPADTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name="configuracion_cotizaciones")
@Table(name = "configuraciones_cotizaciones")
@Setter
@Getter
@NoArgsConstructor
public class ConfiguracionCotizacionesJPADTO {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @NotNull
    private EstrategiaCotizacionJPADTO estrategiaCotizacion;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "login_bonus_id")
    private LoginBonusJPADTO loginBonus;

    public ConfiguracionCotizaciones aModelo() {
        ConfiguracionCotizaciones configuracion = new ConfiguracionCotizaciones();
        configuracion.setId(getId());
        if (getEstrategiaCotizacion() != null) {
            configuracion.setEstrategiaCotizacion(getEstrategiaCotizacion().aModelo());
        }
        if (getLoginBonus() != null) {
            configuracion.setLoginBonus(getLoginBonus().aModelo());
        }
        return configuracion;
    }

    public static ConfiguracionCotizacionesJPADTO desdeModelo(ConfiguracionCotizaciones configuracion) {
        if (configuracion == null) {
            return null;
        }
        ConfiguracionCotizacionesJPADTO dto = new ConfiguracionCotizacionesJPADTO();
        dto.setId(configuracion.getId());
        if (configuracion.getEstrategiaCotizacion() != null) {
            dto.setEstrategiaCotizacion(EstrategiaCotizacionJPADTO.desdeModelo(configuracion.getEstrategiaCotizacion()));
        }
        if (configuracion.getLoginBonus() != null) {
            dto.setLoginBonus(LoginBonusJPADTO.desdeModelo(configuracion.getLoginBonus()));
        }
        return dto;
    }

}
