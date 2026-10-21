package com.overcode.model.transaction;

import com.overcode.model.Player;
import com.overcode.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class Transaction {
    private Long id;
    private User owner;
    private Player player;
    private LocalDateTime date;
    private TransactionType type;
    private Integer quantity;
    private Double unitPrice;

    public Transaction ( User user,Player player, TransactionType type,Integer quantity){
        setDate(LocalDateTime.now());
        setOwner(user);
        setPlayer(player);
        setType(type);
        setQuantity(quantity);
        setUnitPrice(player.getCurrentPrice().doubleValue());
    }

    public Transaction(Long id,User user,Player player,LocalDateTime date, TransactionType type,Integer quantity,Double unitPrice){
        setId(id);
        setOwner(user);
        setPlayer(player);
        setDate(date);
        setType(type);
        setQuantity(quantity);
        setUnitPrice(unitPrice);
    }

    public Double getTotalPrice(){
        return this.unitPrice*this.quantity;
    }
}
