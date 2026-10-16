package com.overcode.persistence.dto.jpa.cotizacion;

import com.overcode.model.cotizacion.registro.RegistroCotizacion;
import com.overcode.persistence.dto.jpa.PlayerJPADTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity(name="registro_cotizacion")
@Table(name = "registros_cotizaciones")
@EntityListeners(AuditingEntityListener.class)
@Setter
@Getter
@NoArgsConstructor
public class RegistroCotizacionJPADTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private PlayerJPADTO player;

    @CreatedDate
    private LocalDateTime createdAt;

    private Double cotizacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private EstrategiaCotizacionJPADTO estrategiaCotizacionUsada;


    public RegistroCotizacion aModelo() {
        RegistroCotizacion registroCotizacion = new RegistroCotizacion();
        registroCotizacion.setCotizacion(this.cotizacion);
        registroCotizacion.setCreatedAt(this.createdAt);
        registroCotizacion.setEstrategiaCotizacionUsada(this.estrategiaCotizacionUsada.aModelo());
        registroCotizacion.setId(this.id);
        registroCotizacion.setPlayer(this.player.aModelo());
        return registroCotizacion;
    }

    public static RegistroCotizacionJPADTO desdeModelo(RegistroCotizacion configuracion) {
        RegistroCotizacionJPADTO dto = new RegistroCotizacionJPADTO();
        EstrategiaCotizacionJPADTO estrategiaCotizacionUsada = EstrategiaCotizacionJPADTO.desdeModelo(configuracion.getEstrategiaCotizacionUsada());
        PlayerJPADTO player = PlayerJPADTO.desdeModelo(configuracion.getPlayer());

        dto.setCotizacion(configuracion.getCotizacion());
        dto.setCreatedAt(configuracion.getCreatedAt());
        dto.setEstrategiaCotizacionUsada(estrategiaCotizacionUsada);
        dto.setId(configuracion.getId());
        dto.setPlayer(player);
        return dto;
    }

}
