package com.stockSync.backend.stock.dto;

import lombok.Data;

@Data
public class StockTransferRequest {
    private Long productId;                         // Que producto estamos moviendo?
    private Long sourcewarehouseId;                 // De que bodega sale? (Origen)
    private Long destinationWarehouseId;            // A que bodega llega (Destino)
    private Integer quantity;                       // Cuantas unidades se transfieren?
}
