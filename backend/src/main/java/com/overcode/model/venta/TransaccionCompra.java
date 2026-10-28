package com.overcode.model.venta;

import com.overcode.model.venta.transaction.TransaccionFinanciera;

import java.util.List;

public record TransaccionCompra(
        List<TransaccionFinanciera> transaccionesFinancieras,
        List<OrdenDeCompra> ordenesCompra
) {
}
