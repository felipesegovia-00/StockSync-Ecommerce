package com.stockSync.backend.stock.dto;

import lombok.Data;

@Data
public class StockRequest {
    private Long productId;
    private Long warehouseId;
    private Integer quantity;
}

