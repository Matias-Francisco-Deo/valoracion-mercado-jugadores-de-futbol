package com.overcode.model.market;

import com.overcode.model.market.orden.OrdenDeVenta;
import com.overcode.model.market.transaction.TransaccionFinanciera;

import java.util.List;

public record TransaccionCompra(
        List<TransaccionFinanciera> transaccionesFinancieras,
        List<OrdenDeVenta> ordenesCompra
) {
}
