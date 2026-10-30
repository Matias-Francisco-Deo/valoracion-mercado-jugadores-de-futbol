package com.overcode.persistence.dto.jpa;

import com.overcode.model.venta.transaction.TransaccionFinanciera;
import com.overcode.model.venta.transaction.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity(name="transaction")
@Table(name = "transactions")
@Setter
@Getter
@NoArgsConstructor
public class TransactionJPADTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserJPADTO owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private PlayerJPADTO player;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, name = "unit_price")
    private Double unitPrice;

    public TransactionJPADTO(Long id,UserJPADTO user,PlayerJPADTO player,LocalDateTime date, TransactionType type,Integer quantity,Double unitPrice){
        setId(id);
        setOwner(user);
        setPlayer(player);
        setDate(date);
        setType(type);
        setQuantity(quantity);
        setUnitPrice(unitPrice);
    }

    public static TransactionJPADTO desdeModelo(TransaccionFinanciera transaccionFinanciera){
        if(transaccionFinanciera == null){return null;}
        return new TransactionJPADTO(
                transaccionFinanciera.getId(),
                UserJPADTO.desdeModelo(transaccionFinanciera.getOwner()),
                PlayerJPADTO.desdeModelo(transaccionFinanciera.getPlayer()),
                transaccionFinanciera.getDate(),
                transaccionFinanciera.getType(),
                transaccionFinanciera.getQuantity(),
                transaccionFinanciera.getUnitPrice()
        );
    }

    public TransaccionFinanciera aModelo() {
        TransaccionFinanciera transaccionFinanciera = new TransaccionFinanciera();
        transaccionFinanciera.setId(this.id);
        transaccionFinanciera.setOwner(this.owner.aModelo());
        transaccionFinanciera.setPlayer(this.player.aModelo());
        transaccionFinanciera.setDate(this.date);
        transaccionFinanciera.setType(this.type);
        transaccionFinanciera.setQuantity(this.quantity);
        transaccionFinanciera.setUnitPrice(this.unitPrice);
        return transaccionFinanciera;
    }

}
